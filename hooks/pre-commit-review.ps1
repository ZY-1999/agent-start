# zy-skills review gate — blocks `git commit` (ZCode / Claude Code PreToolUse on the Bash tool)
# in repos that ran /setup-skills, until the /review skill has approved the current uncommitted diff.
#
# Windows-native PowerShell port of pre-commit-review.sh: ZCode runs `command` hooks through
# cmd.exe on Windows, where a bare `bash` resolves to the WSL launcher and both the script path
# and the injected ZCODE_PROJECT_DIR fail to resolve. PowerShell has no such ambiguity.
#
# stdin  : the PreToolUse hook JSON payload
# exit 0 : allow the tool call (nothing may be written to stdout)
# exit 2 : deny, with the reason on stderr
#
# Approval is recorded by the review skill (skills/dev/review/SKILL.md, step 6):
#   git diff HEAD | git hash-object --stdin > "$(git rev-parse --git-dir)/zy-skills-review-approved"
# The marker is consumed on use and only ever matches one exact diff. The hash pipeline runs
# under `cmd /c` so the bytes piped between the two git processes are identical to the POSIX
# version (PowerShell pipelines re-line-split text and would alter the hash).

$ErrorActionPreference = 'Continue'

$payload = [Console]::In.ReadToEnd()

# Only gate real `git commit` invocations. Parse the payload instead of regex-matching raw JSON:
# any escaped double quote before `git` (e.g. `cd "D:/repo" && git commit`) ended the old
# `"[^"]*"` match early, so the gate silently allowed the commit.
try { $hookInput = $payload | ConvertFrom-Json -ErrorAction Stop } catch { exit 0 }
$command = $hookInput.tool_input.command
if (-not $command) { exit 0 }
if ($command -notmatch '(?m)\bgit\b[^\r\n]*\bcommit\b') { exit 0 }

$projectDir = $env:ZCODE_PROJECT_DIR
if (-not $projectDir) { $projectDir = $env:CLAUDE_PROJECT_DIR }
if ($projectDir) {
    # Git Bash hosts export POSIX paths (/d/projects/x); PowerShell cannot resolve those, and
    # the old code exited 0 on that failure — silently allowing the commit. Translate first,
    # and on any remaining failure fall through to the current directory instead of allowing.
    if ($projectDir -match '^/([a-zA-Z])/(.*)$') {
        $projectDir = $matches[1].ToUpper() + ':\' + ($matches[2] -replace '/', '\')
    }
    try { Set-Location -LiteralPath $projectDir -ErrorAction Stop } catch { }
}

# Repo not opted in (no /setup-skills issue tracker), or not a git repo at all: allow.
if (-not (Test-Path 'docs/agents/issue-tracker.md')) { exit 0 }
$gitDir = (& git rev-parse --git-dir 2>$null | Out-String).Trim()
if ($LASTEXITCODE -ne 0 -or -not $gitDir) { exit 0 }

# Approved hash matches the current uncommitted diff? Consume the approval and allow.
$diffHash = (cmd /c "git diff HEAD | git hash-object --stdin" | Out-String).Trim()
$marker = Join-Path $gitDir 'zy-skills-review-approved'
$approved = $null
if (Test-Path $marker) { $approved = (Get-Content $marker -Raw).Trim() }
if ($approved -and $approved -eq $diffHash) {
    Remove-Item $marker -Force
    exit 0
}

[Console]::Error.WriteLine(@'
git commit blocked by the zy-skills review gate.

This repo ran /setup-skills, so agent-made commits need a passing review first:

1. Run the /review skill against the uncommitted changes (fixed point HEAD, diff `git diff HEAD`).
2. If both axes report no hard violations, /review records approval for this exact diff.
3. Retry the same git commit — it goes through.

Any change to the diff after the review invalidates the approval and review must re-run.
To commit without a review, ask the user to run git commit in their own terminal.
'@)
exit 2
