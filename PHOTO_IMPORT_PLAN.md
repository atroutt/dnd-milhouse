# Photo Import: Spell & Ability Scanner

Take a photo of a spell or ability description and have it automatically parsed and added to the app.

## Steps

### 1. Camera capture
Use `ActivityResultContracts.TakePicture()` to launch the system camera and get back a `Uri` to the captured image. CameraX is an option if a custom viewfinder is ever needed, but the system contract is simpler.

### 2. OCR — text extraction
**ML Kit Text Recognition** (free, fully on-device, no API key).
- Add the dependency, pass the image URI, get back raw text.
- Handles printed text from books, PDFs, and character sheets well.

### 3. Parsing — raw text → structured data
This is the hardest part. Two options:

**Option A: Regex/rules-based parser**
- Works well for clean text from D&D Beyond printouts or the PHB.
- Brittle against messy images, homebrew formatting, or handwritten notes.

**Option B: Claude API (recommended)**
- Send OCR output to Claude with a prompt to extract fields as JSON.
- Much more resilient to varied formatting and homebrew content.
- Costs a fraction of a cent per scan.
- Maps cleanly to the existing models (Spell has 13 fields, Ability has 8).

Example prompt shape:
```
Extract the following fields from this spell description and return JSON:
name, level, school, castingTime, range, duration, components,
materialComponents, description, higherLevels, isConcentration, isRitual
---
<OCR text here>
```

### 4. Review screen before saving
Open the existing spell/ability edit screen pre-populated with parsed data so the user can correct any OCR or parsing errors before saving. No new screen needed — just a new navigation path into the existing edit screens.

## Effort summary

| Piece | Effort |
|---|---|
| Camera capture button | Small |
| ML Kit OCR integration | Small–Medium (one-time setup) |
| Claude API parsing | Small (one API call, JSON response) |
| Review/confirm UI | Small (reuse existing edit screens) |
| Wiring it all together | Medium |

## Key decision
Regex vs. Claude API for step 3. Use Claude if the feature needs to handle physical books, homebrew PDFs, or anything outside a perfectly consistent format.
