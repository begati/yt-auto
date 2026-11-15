// Spec ad-block: the document-start script, run in a bare VM context standing in for the page.
import { test } from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import vm from 'node:vm'

const SCRIPT = readFileSync(new URL('../../main/assets/page_start.js', import.meta.url), 'utf8')
const AD_FIELDS = ['adPlacements', 'adSlots', 'playerAds', 'adBreakHeartbeatParams']

// A page with the `yta` bridge answering [flags] (no bridge when flags is null), then the script.
function page(flags = { skipAds: true }) {
  const ctx = vm.createContext({})
  ctx.window = ctx
  // Response.json in a browser parses natively, not through JSON.parse: the stand-in returns the object as is.
  vm.runInContext('class Response{constructor(o){this.o=o}json(){return Promise.resolve(this.o)}}', ctx)
  if (flags) ctx.yta = { flag: k => !!flags[k] }
  vm.runInContext(SCRIPT, ctx)
  return ctx
}

const withAds = () => ({
  videoDetails: { videoId: 'abc' },
  streamingData: { formats: [1] },
  adPlacements: [{ x: 1 }], adSlots: [{ y: 1 }], playerAds: [{ z: 1 }], adBreakHeartbeatParams: 'p',
})
const noAdFields = o => AD_FIELDS.filter(k => k in o)

test('ADS-01 JSON.parse drops the ad fields', () => {
  const ctx = page()
  const out = vm.runInContext(`JSON.parse(${JSON.stringify(JSON.stringify(withAds()))})`, ctx)
  assert.deepEqual(noAdFields(out), [])
})

test('ADS-02 Response.json drops the ad fields', async () => {
  const ctx = page()
  ctx.body = withAds()
  const out = await vm.runInContext('new Response(body).json()', ctx)
  assert.deepEqual(noAdFields(out), [])
})

test('ADS-03 ytInitialPlayerResponse is stored without the ad fields', () => {
  const ctx = page()
  vm.runInContext(`var ytInitialPlayerResponse = ${JSON.stringify(withAds())};`, ctx)
  const out = vm.runInContext('window.ytInitialPlayerResponse', ctx)
  assert.deepEqual(noAdFields(out), [])
  assert.equal(out.videoDetails.videoId, 'abc')
})

test('ADS-04 other fields and ad-free responses stay intact', async () => {
  const ctx = page()
  const out = vm.runInContext(`JSON.parse(${JSON.stringify(JSON.stringify(withAds()))})`, ctx)
  assert.deepEqual(JSON.parse(JSON.stringify(out)), { videoDetails: { videoId: 'abc' }, streamingData: { formats: [1] } })
  const clean = { a: 1, b: { adPlacements: [1] } } // only top-level fields are ad data
  assert.deepEqual(JSON.parse(JSON.stringify(vm.runInContext(`JSON.parse('${JSON.stringify(clean)}')`, ctx))), clean)
  ctx.body = { a: 1 }
  assert.deepEqual(JSON.parse(JSON.stringify(await vm.runInContext('new Response(body).json()', ctx))), { a: 1 })
})

test('ADS-05 with the flag off responses pass unchanged', async () => {
  const ctx = page({ skipAds: false })
  const parsed = vm.runInContext(`JSON.parse(${JSON.stringify(JSON.stringify(withAds()))})`, ctx)
  assert.deepEqual(noAdFields(parsed), AD_FIELDS)
  ctx.body = withAds()
  assert.deepEqual(noAdFields(await vm.runInContext('new Response(body).json()', ctx)), AD_FIELDS)
  vm.runInContext(`var ytInitialPlayerResponse = ${JSON.stringify(withAds())};`, ctx)
  assert.deepEqual(noAdFields(vm.runInContext('ytInitialPlayerResponse', ctx)), AD_FIELDS)
})

test('ADS-13 a nested playerResponse loses the ad fields, alone or inside a list', async () => {
  const ctx = page()
  const list = vm.runInContext(`JSON.parse(${JSON.stringify(JSON.stringify([{ playerResponse: withAds() }, { watchNextResponse: { adPlacements: 1 } }]))})`, ctx)
  assert.deepEqual(noAdFields(list[0].playerResponse), [])
  assert.equal(list[0].playerResponse.videoDetails.videoId, 'abc')
  assert.deepEqual(JSON.parse(JSON.stringify(list[1])), { watchNextResponse: { adPlacements: 1 } }) // only playerResponse is ad data
  ctx.body = { playerResponse: withAds() }
  assert.deepEqual(noAdFields((await vm.runInContext('new Response(body).json()', ctx)).playerResponse), [])
  const off = page({ skipAds: false })
  const kept = vm.runInContext(`JSON.parse(${JSON.stringify(JSON.stringify([{ playerResponse: withAds() }]))})`, off)
  assert.deepEqual(noAdFields(kept[0].playerResponse), AD_FIELDS)
})

test('EDGE non-object responses pass unchanged', () => {
  const ctx = page()
  for (const s of ['42', '"adPlacements"', '[{"adPlacements":1}]', 'null', 'true']) {
    assert.deepEqual(JSON.parse(JSON.stringify(vm.runInContext(`JSON.parse('${s}')`, ctx))), JSON.parse(s))
  }
})

test('EDGE without yta the flag reads as on', () => {
  const ctx = page(null)
  const out = vm.runInContext(`JSON.parse(${JSON.stringify(JSON.stringify(withAds()))})`, ctx)
  assert.deepEqual(noAdFields(out), [])
})
