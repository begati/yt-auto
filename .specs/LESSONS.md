# LESSONS - auto-maintained by scripts/lessons.py

> Machine-owned. Do NOT hand-edit. Changes are overwritten on the next `lessons.py` write.
> Canonical state lives in `.specs/lessons.json`. Edit lessons only via the script.
> promote_threshold=2 distinct features · window_days=45 · quarantine_threshold=2

## Confirmed (load these at Plan/Checks)

Corroborated across multiple features. Safe to apply as guidance.

_none_

## Candidates (under observation - do NOT load as guidance yet)

Seen once or not yet corroborated. Tracked, not trusted.

### L-001 - When a criterion lists alternative input sources (fallback chain), give each alternative its own asserted case; one green case leaves the others unpinned.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `parser` · harmful: 0
- features: car-features-verification
- evidence: FEED-08 / C8 (parser)
- last seen: 2026-11-10T22:34:43Z

### L-002 - When a criterion says a state is discarded, test the round trip (change away and back); masking it against the current value passes a one-way test.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `settings` · harmful: 0
- features: video-fill
- evidence: ASPECT-09 / C13 (settings)
- last seen: 2026-11-11T15:19:41Z

### L-003 - A setting shown on both car and phone needs a proof for each surface; the phone list is built separately from CHOICES.
- signal: `ac_gap` · recurrence: 1 feature(s) · scope: `settings` · harmful: 0
- features: video-fill
- evidence: ASPECT-06 / C10 (settings)
- last seen: 2026-11-11T15:19:41Z

## Quarantined (failed when applied - ignore)

A confirmed lesson that recurred alongside failure. Kept for the maintainer to review.

_none_
