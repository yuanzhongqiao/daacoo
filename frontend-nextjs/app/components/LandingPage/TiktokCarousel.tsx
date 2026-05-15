"use client";

import { useEffect, useRef } from "react";

export default function TikTokEmbed() {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (containerRef.current) {
      // Clear any existing content first
      containerRef.current.innerHTML = '';
      
      // Create a new blockquote element
      const blockquote = document.createElement('blockquote');
      blockquote.className = 'tiktok-embed';
      blockquote.setAttribute('cite', 'https://www.tiktok.com/@elatoai/video/7487016680925744406');
      blockquote.setAttribute('data-video-id', '7487016680925744406');
      blockquote.style.maxWidth = '605px';
      blockquote.style.minWidth = '325px';
      blockquote.style.margin = '0 auto';
      
      // Create section element
      const section = document.createElement('section');
      blockquote.appendChild(section);
      
      // Add the blockquote to the container
      containerRef.current.appendChild(blockquote);
      
      // Check if script already exists and remove it
      const existingScript = document.querySelector('script[src="https://www.tiktok.com/embed.js"]');
      if (existingScript) {
        existingScript.remove();
      }
      
      // Load the TikTok embed script
      const script = document.createElement('script');
      script.src = 'https://www.tiktok.com/embed.js';
      script.async = true;
      document.body.appendChild(script);
      
      // Cleanup function
      return () => {
        if (containerRef.current) {
          containerRef.current.innerHTML = '';
        }
        if (script.parentNode) {
          script.parentNode.removeChild(script);
        }
      };
    }
  }, []);

  return (
    <div className="w-full h-full flex items-center justify-center" ref={containerRef}>
      {/* TikTok embed will be inserted here by useEffect */}
    </div>
  );
}