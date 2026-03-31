import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

export function fadeInUp(el, delay = 0) {
  gsap.fromTo(el, { opacity: 0, y: 30 }, { opacity: 1, y: 0, duration: 0.5, delay, ease: 'power2.out' })
}

export function staggerFadeIn(els, stagger = 0.08) {
  gsap.fromTo(els, { opacity: 0, y: 24 }, { opacity: 1, y: 0, duration: 0.45, stagger, ease: 'power2.out' })
}

export function scaleIn(el, delay = 0) {
  gsap.fromTo(el, { opacity: 0, scale: 0.92 }, { opacity: 1, scale: 1, duration: 0.4, delay, ease: 'back.out(1.4)' })
}

export function slideInLeft(el, delay = 0) {
  gsap.fromTo(el, { opacity: 0, x: -40 }, { opacity: 1, x: 0, duration: 0.5, delay, ease: 'power3.out' })
}

export function pulseOnce(el) {
  gsap.fromTo(el, { scale: 1 }, { scale: 1.15, duration: 0.15, yoyo: true, repeat: 1, ease: 'power1.inOut' })
}

export function countUp(el, target, duration = 1.2) {
  gsap.to(el, {
    innerText: target,
    duration,
    snap: { innerText: 1 },
    ease: 'power1.out'
  })
}

export { gsap, ScrollTrigger }
