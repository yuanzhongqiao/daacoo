"use client";
import React, { useState, useEffect, useRef } from "react";
import Typed from "typed.js";

const AnimatedText: React.FC = () => {
    const el = useRef(null);

    useEffect(() => {
        const options = {
            strings: [
                '<span class="text-blue-500">with interactive storytelling</span>',
                '<span class="text-purple-500">for language learning</span>',
                '<span class="text-green-500">for bedtime stories</span>',
                '<span class="text-orange-500">with educational adventures</span>',
                '<span class="text-pink-500">for vocabulary building</span>',
                '<span class="text-teal-500">as a reading companion</span>',
            ],
            typeSpeed: 70,  // Slightly faster
            backSpeed: 30,  // Add some backspeed for playfulness
            backDelay: 800, // Shorter delay
            cursorChar: '🪄', // Child-friendly cursor
            fadeOut: true,
            fadeOutDelay: 1,
            loop: true,
        };

        const typed = new Typed(el.current, options);

        return () => {
            typed.destroy();
        };
    }, []);

    return (
        <h1 className="text-3xl font-semibold font-borel h-24 min-h-[6rem] flex items-center justify-center">
            <span ref={el} className="inline-block" />
        </h1>
    );
};

export default AnimatedText;
