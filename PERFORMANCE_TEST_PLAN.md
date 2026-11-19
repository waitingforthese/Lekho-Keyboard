# Lekho Keyboard V1.2 — Manual QA Plan

| Area | Test | Expected result |
|---|---|---|
| UI | Open keyboard in a note editor | Toolbar, numeric row, QWERTY rows and bottom row render with pale lavender panel and white rounded keys |
| Layout | Test portrait with 3-button navigation | Bottom key row remains above system navigation controls |
| Layout | Test gesture navigation | No key overlaps gesture/navigation strip |
| Shift | Tap Shift once, then `a` | Uppercase `A` is inserted; remaining keys return to lowercase |
| Caps Lock | Double-tap Shift, then type `abc` | `ABC` inserted and all key labels remain uppercase until Shift is tapped again |
| Backspace | Tap once | One Unicode grapheme is deleted |
| Backspace | Hold for 2 seconds | Continuous deletion occurs until release |
| Backspace | Hold over Devanagari word with matras | Grapheme-aware deletion; no stuck repeat after release |
| Enter | Multiline note field | New line inserted |
| Enter | Messaging field with Send IME action | Editor action invoked, fallback Enter sent if action is not handled |
| Language | Tap globe/language toolbar | Marathi/English mode toggles |
| Marathi | Type `yashsvi` | `यशस्वी` appears as a suggestion |
| History | Type a word and press Space/Enter | Committed word saved to local history |
| History | Open keyboard settings | Up to 100 recent words shown, newest first |
| History | Disable history | Existing history cleared; new words not saved |
| Privacy | Type in password field | Suggestions and history suppressed |
| Clipboard | Tap clipboard icon in normal text field | Clipboard contents pasted |
| Clipboard | Focus password field | Clipboard shortcut does not paste |

## Performance targets for device testing
- Key press to visible committed character: target p95 < 80 ms on the target device.
- Suggestion strip refresh: target p95 < 50 ms for the starter dictionary.
- No ANR, crash, stuck Backspace repeat, or key-row overlap during a 10-minute typing test.

Record actual Android version, device model, navigation mode and editor app for every failure.
