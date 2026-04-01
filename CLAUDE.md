# CLAUDE.md

This file provides guidance for AI assistants (Claude Code and others) working in this repository.

## Repository Overview

**Name:** ai-agent-test  
**Purpose:** Test repository for AI agent workflows and Claude Code integration experiments.  
**Status:** Early-stage / minimal — no source code yet.

## Current State

This repository currently contains:

- `README.md` — minimal project description
- `.gitignore` — standard .NET/.NET Core template (indicates planned .NET development)
- `CLAUDE.md` — this file

There is no source code, dependency files, tests, or CI/CD configuration yet.

## Intended Stack (Inferred)

Based on the `.gitignore` pattern, this project is likely intended to be a **.NET** application. Common indicators:
- Ignores `bin/`, `obj/`, `Debug/`, `Release/` directories
- Ignores `.nupkg`, `.snupkg` NuGet package artifacts
- Ignores NUnit test result files and MSBuild outputs

If the stack changes, update this section accordingly.

## Development Workflow

Since no build system exists yet, once source code is added, update this section with:
- How to build: e.g., `dotnet build`
- How to run tests: e.g., `dotnet test`
- How to run the app: e.g., `dotnet run`

## Branch Strategy

- `main` — stable/production branch
- Feature branches should be created from `main` and merged back via pull request

The current working branch for documentation is `claude/add-claude-documentation-gMymB`.

## Conventions for AI Assistants

1. **Do not invent code** based on assumptions about the intended project — wait for actual source files.
2. **Update this file** whenever the project's structure, stack, or conventions change significantly.
3. **Prefer small, targeted commits** with descriptive messages.
4. **Do not push to `main` directly** — use feature branches and pull requests.
5. **Check for existing patterns** before adding new dependencies or configuration.

## Git Remotes

- `origin` → `tlejmi/ai-agent-test` (GitHub)

## Contact

Repository owner: Tarek Lejmi (`tlejmi`)
