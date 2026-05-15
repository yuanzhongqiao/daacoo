'use client'

import React, { useEffect, useRef, useState } from 'react';
import {  IoIosShareAlt } from 'react-icons/io';
import { Wand, Heart, MessageCircle, Bookmark } from 'lucide-react';
interface VideoPlayerProps {
  sources: string[];
  className?: string;
}

const ICON_SIZE = 28;

export default function VideoPlayer({ sources, className = "" }: VideoPlayerProps) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [currentSourceIndex, setCurrentSourceIndex] = useState(0);
  
  useEffect(() => {
    const videoElement = videoRef.current;
    if (!videoElement) return;
    
    // Handle video ended event to switch to the next video
    const handleVideoEnded = () => {
      // Fade out
      if (videoElement) {
        videoElement.classList.add('opacity-0');
        
        // Wait for fade out, then change source and fade in
        setTimeout(() => {
          setCurrentSourceIndex((prevIndex) => (prevIndex + 1) % sources.length);
        }, 300);
      }
    };
    
    videoElement.addEventListener('ended', handleVideoEnded);
    
    // Clean up event listener
    return () => {
      videoElement.removeEventListener('ended', handleVideoEnded);
    };
  }, [sources.length]);
  
  // When the current source index changes, load and play the new video
  useEffect(() => {
    const videoElement = videoRef.current;
    if (!videoElement) return;
    
    videoElement.load();
    videoElement.play().catch(error => {
      console.error("Error playing video:", error);
    });
    
    // Fade in after source change
    setTimeout(() => {
      videoElement.classList.remove('opacity-0');
    }, 50);
    
  }, [currentSourceIndex]);

  return (
    <div className="relative aspect-[9/16] max-h-[85vh] w-full">
      <video 
        ref={videoRef}
        className={`w-full h-full object-cover rounded-xl shadow-lg transition-opacity duration-300 ${className}`}
        autoPlay 
        muted 
        playsInline
      >
        <source src={sources[currentSourceIndex]} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
	  	   {/* TikTok-style icons */}
			 <div className="absolute top-2 left-2 flex flex-col gap-4">
                        <button className="bg-transparent bg-opacity-50 rounded-full p-2 transition-transform hover:scale-110">
                                <Wand size={ICON_SIZE+4} color="white" />
                            </button>
                        </div>
                        <div className="absolute bottom-2 right-2 flex flex-col gap-4">
                        <button className="bg-transparent bg-opacity-50 rounded-full p-2 transition-transform hover:scale-110">
                                <Heart size={ICON_SIZE} color="rgb(239, 68, 68" fill="rgb(239, 68, 68)"  />
                            </button>
                            
                            <button className="bg-transparent bg-opacity-50 rounded-full p-2 transition-transform hover:scale-110">
                                <MessageCircle size={ICON_SIZE} color="white" />
                            </button>

                            <button className="bg-transparent bg-opacity-50 rounded-full p-2 transition-transform hover:scale-110">
                                <Bookmark size={ICON_SIZE} color="rgb(250, 204, 21)" fill="rgb(250, 204, 21)"  />
                            </button>
                            
                            <button className="bg-transparent bg-opacity-50 rounded-full p-2 transition-transform hover:scale-110">
                                <IoIosShareAlt size={ICON_SIZE} color="white" />
                            </button>
                        </div>
    </div>
  );
}