import gsap from 'gsap';

/** Retorna uma função de limpeza — chamar em ngOnDestroy pra remover os listeners. */
export function createMagneticHover(element: HTMLElement, strength = 0.35): () => void {
  const moveX = gsap.quickTo(element, 'x', { duration: 0.5, ease: 'power3.out' });
  const moveY = gsap.quickTo(element, 'y', { duration: 0.5, ease: 'power3.out' });

  const onMouseMove = (event: MouseEvent) => {
    const rect = element.getBoundingClientRect();
    const relativeX = event.clientX - (rect.left + rect.width / 2);
    const relativeY = event.clientY - (rect.top + rect.height / 2);
    moveX(relativeX * strength);
    moveY(relativeY * strength);
  };

  const onMouseLeave = () => {
    gsap.to(element, { x: 0, y: 0, duration: 0.6, ease: 'elastic.out(1, 0.4)' });
  };

  element.addEventListener('mousemove', onMouseMove);
  element.addEventListener('mouseleave', onMouseLeave);

  return () => {
    element.removeEventListener('mousemove', onMouseMove);
    element.removeEventListener('mouseleave', onMouseLeave);
  };
}
