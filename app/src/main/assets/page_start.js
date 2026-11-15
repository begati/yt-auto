// Runs at the start of every m.youtube.com document in the car, before the page's own scripts (see ad-block spec).
// Removes the ad data from the player responses, so the player never schedules an ad, while "skipAds" is on.
// The responses come through JSON.parse and Response.json (SPA navigation: /youtubei/v1/player, get_watch) and, on a full
// load, as the inline `var ytInitialPlayerResponse = {...}`.
// ponytail: tied to YouTube's player response fields; re-check via chrome://inspect if ads come back.
(() => {
  if (window.__ytaStart) return
  window.__ytaStart = 1
  // The bridge may not be there yet: then the flag's default.
  const flag = (key, dflt) => { try { return !!yta.flag(key) } catch (e) { return dflt } }
  const AD_FIELDS = ['adPlacements', 'adSlots', 'playerAds', 'adBreakHeartbeatParams']
  const clean = o => {
    if (o && typeof o === 'object' && !Array.isArray(o) && AD_FIELDS.some(k => k in o) && flag('skipAds', true)) {
      for (const k of AD_FIELDS) delete o[k]
    }
  }
  // A player response, or one nested as `playerResponse` (SPA navigation's /youtubei/v1/get_watch answers
  // [{playerResponse}, {watchNextResponse}]).
  const strip = o => {
    if (Array.isArray(o)) o.forEach(i => i && clean(i.playerResponse))
    else if (o && typeof o === 'object') { clean(o); clean(o.playerResponse) }
    return o
  }
  const parse = JSON.parse
  JSON.parse = function () { return strip(parse.apply(this, arguments)) }
  const json = Response.prototype.json
  Response.prototype.json = function () { return json.apply(this, arguments).then(strip) }
  let initial
  Object.defineProperty(window, 'ytInitialPlayerResponse', {
    configurable: true, get: () => initial, set: v => { initial = strip(v) },
  })
})()
