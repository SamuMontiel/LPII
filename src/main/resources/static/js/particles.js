// particles.js - VirtualHub (fondo discreto)
(function () {
  const MAX_PARTICLES = 80;           // tope en pantallas grandes
  const MIN_PARTICLES = 18;           // tope en pantallas pequeñas
  const PARTICLE_SIZE = { min: 0.6, max: 2.2 };
  const SPEED = { min: 0.15, max: 0.6 };
  const LINK_DISTANCE = 120;
  const LINK_OPACITY = 0.12;

  let canvas, ctx, particles = [], w, h, dpr;

  function createCanvas() {
    canvas = document.createElement('canvas');
    canvas.id = 'vh-particles-canvas';
    document.body.appendChild(canvas);
    ctx = canvas.getContext('2d');
    resize();
    window.addEventListener('resize', resize, { passive: true });
  }

  function resize() {
    dpr = Math.max(1, window.devicePixelRatio || 1);
    w = window.innerWidth;
    h = window.innerHeight;
    canvas.width = Math.round(w * dpr);
    canvas.height = Math.round(h * dpr);
    canvas.style.width = w + 'px';
    canvas.style.height = h + 'px';
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    initParticles();
  }

  function rand(min, max) {
    return Math.random() * (max - min) + min;
  }

  function initParticles() {
    particles = [];
    const area = w * h;
    const base = Math.sqrt(area) / 10;
    let count = Math.round(Math.max(MIN_PARTICLES, Math.min(MAX_PARTICLES, base)));
    for (let i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * w,
        y: Math.random() * h,
        vx: (Math.random() - 0.5) * rand(SPEED.min, SPEED.max),
        vy: (Math.random() - 0.5) * rand(SPEED.min, SPEED.max),
        r: rand(PARTICLE_SIZE.min, PARTICLE_SIZE.max),
        alpha: rand(0.35, 0.95),
        hue: rand(195, 210) // tonos azul-cian
      });
    }
  }

  function draw() {
    ctx.clearRect(0, 0, w, h);

    // partículas
    for (let p of particles) {
      ctx.beginPath();
      const grad = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.r * 6);
      grad.addColorStop(0, `rgba(107,224,255,${p.alpha * 0.9})`);
      grad.addColorStop(0.4, `rgba(0,176,255,${p.alpha * 0.45})`);
      grad.addColorStop(1, `rgba(6,18,36,0)`);
      ctx.fillStyle = grad;
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      ctx.fill();
    }

    // líneas sutiles entre partículas cercanas
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const a = particles[i];
        const b = particles[j];
        const dx = a.x - b.x;
        const dy = a.y - b.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < LINK_DISTANCE) {
          const o = (1 - dist / LINK_DISTANCE) * LINK_OPACITY * Math.min(a.alpha, b.alpha);
          ctx.strokeStyle = `rgba(107,224,255,${o})`;
          ctx.lineWidth = 0.8;
          ctx.beginPath();
          ctx.moveTo(a.x, a.y);
          ctx.lineTo(b.x, b.y);
          ctx.stroke();
        }
      }
    }
  }

  function step() {
    for (let p of particles) {
      p.x += p.vx;
      p.y += p.vy;

     
      p.vx += Math.sin((p.x + performance.now() * 0.0002) * 0.002) * 0.0008;
      p.vy += Math.cos((p.y + performance.now() * 0.0003) * 0.002) * 0.0008;

   
      if (p.x < -20) p.x = w + 20;
      if (p.x > w + 20) p.x = -20;
      if (p.y < -20) p.y = h + 20;
      if (p.y > h + 20) p.y = -20;
    }
    draw();
    requestAnimationFrame(step);
  }

  // inicia todo cuando DOM listo
  function init() {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => { createCanvas(); step(); });
    } else {
      createCanvas();
      step();
    }
  }

  // Exponer init para control si se necesita
  window.VHParticles = { init };
  init();
})();
