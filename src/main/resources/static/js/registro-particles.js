// registro-particles.js - Partículas discretas para el fondo de la página de registro (mejorado)
(function () {
  'use strict';

  const CONFIG = {
    MIN: 8,
    MAX: 36,
    SIZE: { min: 0.8, max: 1.6 },
    SPEED: { min: 0.02, max: 0.14 },
    ALPHA: { min: 0.05, max: 0.16 },
    RESIZE_DEBOUNCE_MS: 120
  };

  let canvas = null;
  let ctx = null;
  let particles = [];
  let rafId = null;
  let resizeTimer = null;
  let reducedMotion = false;
  let w = 0;
  let h = 0;
  let dpr = 1;

  const rand = (a, b) => Math.random() * (b - a) + a;
  const clamp = (v, a, b) => Math.max(a, Math.min(b, v));

  function log(...args) {
    if (window && window.console && console.log) console.log('[RegistroParticles]', ...args);
  }
  function warn(...args) { if (window && window.console && console.warn) console.warn('[RegistroParticles]', ...args); }
  function error(...args) { if (window && window.console && console.error) console.error('[RegistroParticles]', ...args); }

  function ensureCanvas() {
    try {
      const existing = document.getElementById('vh-reg-bg-canvas');
      if (existing) {
        canvas = existing;
        ctx = canvas.getContext('2d');
        log('Canvas found in DOM');
        return;
      }

      canvas = document.createElement('canvas');
      canvas.id = 'vh-reg-bg-canvas';

      // Inline styles as fallback if CSS not loaded
      Object.assign(canvas.style, {
        position: 'fixed',
        inset: '0',
        zIndex: '0',
        pointerEvents: 'none',
        opacity: '0.5',
        mixBlendMode: 'screen',
        transition: 'opacity .3s ease',
        display: 'block'
      });

      // Insert as first child so it stays behind content with higher z-index
      const body = document.body || document.getElementsByTagName('body')[0];
      if (!body) {
        warn('document.body not available yet');
        return;
      }
      if (body.firstChild) body.insertBefore(canvas, body.firstChild);
      else body.appendChild(canvas);

      ctx = canvas.getContext('2d');
      log('Canvas created and appended to body');
    } catch (e) {
      error('ensureCanvas error', e);
    }
  }

  function setupListeners() {
    try {
      // prefers-reduced-motion
      if (window.matchMedia) {
        const mq = window.matchMedia('(prefers-reduced-motion: reduce)');
        reducedMotion = mq.matches;
        const handler = (e) => {
          reducedMotion = e.matches;
          log('prefers-reduced-motion changed:', reducedMotion);
          if (reducedMotion) stop(); else start();
        };
        if (mq.addEventListener) mq.addEventListener('change', handler);
        else if (mq.addListener) mq.addListener(handler);
      }

      // resize debounce
      window.addEventListener('resize', () => {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(resize, CONFIG.RESIZE_DEBOUNCE_MS);
      }, { passive: true });

      // pause when tab hidden
      document.addEventListener('visibilitychange', () => {
        log('visibilitychange:', document.hidden);
        if (document.hidden) stop();
        else start();
      });

      log('Listeners set up');
    } catch (e) {
      error('setupListeners error', e);
    }
  }

  function resize() {
    try {
      if (!canvas) ensureCanvas();
      if (!canvas || !ctx) {
        warn('Canvas or context missing on resize');
        return;
      }
      dpr = Math.max(1, window.devicePixelRatio || 1);
      w = window.innerWidth;
      h = window.innerHeight;
      canvas.width = Math.round(w * dpr);
      canvas.height = Math.round(h * dpr);
      canvas.style.width = w + 'px';
      canvas.style.height = h + 'px';
      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
      initParticles();
      log('Resized canvas to', canvas.width, 'x', canvas.height, 'dpr', dpr, 'particles', particles.length);
      if (!reducedMotion && !document.hidden) start();
    } catch (e) {
      error('resize error', e);
    }
  }

  function initParticles() {
    particles.length = 0;
    const area = w * h;
    const base = Math.sqrt(area) / 22;
    const count = Math.round(clamp(base, CONFIG.MIN, CONFIG.MAX));
    for (let i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * w,
        y: Math.random() * h,
        r: rand(CONFIG.SIZE.min, CONFIG.SIZE.max),
        vx: (Math.random() - 0.5) * rand(CONFIG.SPEED.min, CONFIG.SPEED.max),
        vy: rand(CONFIG.SPEED.min * 0.2, CONFIG.SPEED.max * 0.6),
        alpha: rand(CONFIG.ALPHA.min, CONFIG.ALPHA.max),
        phase: Math.random() * Math.PI * 2
      });
    }
  }

  function draw() {
    try {
      if (!ctx) return;
      ctx.clearRect(0, 0, w, h);
      for (let p of particles) {
        p.phase += 0.002;
        const floatY = Math.sin(p.phase) * (p.r * 0.7);
        const grad = ctx.createRadialGradient(p.x, p.y + floatY, 0, p.x, p.y + floatY, p.r * 6);
        grad.addColorStop(0, `rgba(107,224,255,${p.alpha})`);
        grad.addColorStop(0.6, `rgba(0,176,255,${p.alpha * 0.35})`);
        grad.addColorStop(1, `rgba(6,18,36,0)`);
        ctx.fillStyle = grad;
        ctx.beginPath();
        ctx.arc(p.x, p.y + floatY, p.r, 0, Math.PI * 2);
        ctx.fill();
      }
    } catch (e) {
      error('draw error', e);
    }
  }

  function step() {
    try {
      for (let p of particles) {
        p.x += p.vx;
        p.y += p.vy * (0.6 + Math.sin(p.phase) * 0.35);
        if (p.x < -20) p.x = w + 20;
        if (p.x > w + 20) p.x = -20;
        if (p.y < -40) p.y = h + 40;
        if (p.y > h + 40) p.y = -40;
      }
      draw();
      rafId = requestAnimationFrame(step);
    } catch (e) {
      error('step error', e);
    }
  }

  function start() {
    try {
      if (reducedMotion || document.hidden) {
        log('start aborted: reducedMotion or hidden');
        return;
      }
      if (rafId) cancelAnimationFrame(rafId);
      rafId = requestAnimationFrame(step);
      log('animation started');
    } catch (e) {
      error('start error', e);
    }
  }

  function stop() {
    try {
      if (rafId) cancelAnimationFrame(rafId);
      rafId = null;
      log('animation stopped');
    } catch (e) {
      error('stop error', e);
    }
  }

  function api() {
    return {
      start,
      stop,
      refresh: resize,
      canvasId: canvas ? canvas.id : null,
      particleCount: () => particles.length
    };
  }

  function init() {
    try {
      log('init called, document.readyState', document.readyState);
      ensureCanvas();
      setupListeners();
      resize();
      // expose API
      window.RegistroParticles = api();
      log('RegistroParticles ready', window.RegistroParticles.canvasId);
    } catch (err) {
      error('RegistroParticles init error:', err);
    }
  }

  // Try DOMContentLoaded then fallback to load
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
    window.addEventListener('load', () => { if (!window.RegistroParticles) init(); });
  } else {
    init();
  }
})();
