# Guidelines for AI Assistance

## Core Objective
Generate concise, clean, and well-documented code. Focus ONLY on delivering the requested features without fluff.

## Token Optimization Rules
- **Do not reiterate code:** If you are modifying a file, only return the specific modified block or function, rather than outputting the entire file contents.
- **Limit explanations:** Provide a 1-2 sentence summary of your changes. Skip verbose, step-by-step breakdowns of standard programming logic unless explicitly asked.
- **Leverage repo context:** Assume familiarity with existing project structures and do not output unnecessary boilerplate.
- **One-shot prompting:** Aim to answer the prompt accurately on the first attempt to avoid follow-up corrections.
