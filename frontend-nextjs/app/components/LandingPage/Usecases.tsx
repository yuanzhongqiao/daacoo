"use client";

import { r2Url } from "@/lib/data";

export default function Usecases() {
    const usecases = [
        {
            title: "For Interactive Storytelling",
            description: "Transform ordinary toys into storytelling companions that respond to your child's imagination.",
            features: [
                "Voice-activated responses",
                "Customizable personalities",
                "Age-appropriate content",
                "Multiple story modes"
            ],
            videoSrc: `${r2Url}/peterrabbit.mp4`,
            poster: `${process.env.NEXT_PUBLIC_SUPABASE_URL}/storage/v1/object/public/images/peterrabbit.png`
        },
        {
            title: "For Educational Learning",
            description: "Turn everyday objects into educational tools that make learning fun and interactive.",
            features: [],
            videoSrc: `${r2Url}/paddington.mp4`,
            poster: `${process.env.NEXT_PUBLIC_SUPABASE_URL}/storage/v1/object/public/images/paddington.png`
        },
        {
            title: "For Creative Play",
            description: "Enhance playtime with responsive objects that encourage creativity and imaginative play.",
            features: [
                "Character role-playing",
                "Collaborative play options",
                "Parent monitoring features"
            ],
            videoSrc: `${r2Url}/plant.mp4`,
            poster: `${process.env.NEXT_PUBLIC_SUPABASE_URL}/storage/v1/object/public/images/plant.png`
        }
    ];

    return (
        <section className="py-16 bg-stone-50">
            <div className="max-w-6xl mx-auto px-4 sm:px-6">
                <div className="space-y-16 md:space-y-24">
                    {usecases.map((usecase, index) => (
                        <div 
                            key={index} 
                            className={`flex flex-col ${index % 2 === 0 ? 'md:flex-row' : 'md:flex-row-reverse'} gap-16 items-center`}
                        >
                            {/* Video Column */}
                            <div className="w-full md:w-1/2">
                                <div className="relative aspect-video rounded-2xl overflow-hidden shadow-lg">
                                    <video 
                                        className="w-full h-full object-cover"
                                        controls
                                        poster={usecase.poster}
                                    >
                                        <source src={usecase.videoSrc} type="video/mp4" />
                                        Your browser does not support the video tag.
                                    </video>
                                </div>
                            </div>

                            {/* Text Column */}
                            <div className="w-full md:w-1/2">
                                <h3 className="text-3xl md:text-4xl font-semibold text-stone-800 mb-4 font-silkscreen">
                                    {usecase.title}
                                </h3>
                                <p className="text-xl text-gray-600 mb-6">
                                    {usecase.description}
                                </p>
                                <div className="space-y-3">
                                    {/* <h4 className="text-lg font-medium text-stone-800">Key Features:</h4> */}
                                    <ul className="space-y-2">
                                        {usecase.features.map((feature, idx) => (
                                            <li key={idx} className="flex items-start">
                                                <svg className="w-5 h-5 text-emerald-500 mr-2 mt-1 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                                                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                                                </svg>
                                                <span className="text-gray-400 text-xl">{feature}</span>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
}