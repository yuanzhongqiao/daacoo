import { User } from "@supabase/supabase-js";
import AnimatedText from "./AnimatedText";
import { Emoji } from "./Emoji";
/**
 * Headlines for the landing page
 *
 * "No more boring AI conversations with a chatbot",
 * "No more boring AI conversations on a screen",
 * "No more boring AI chatbots",
 * "Experience AI conversations that feel real",
 * "No more boring AI chatbot convos",
 * "A new home for your AI characters",
 * "Hear a joke whenever you need a pick-me-up",
 * "Your AI, better at debates than Congress",
 * "Think TMZ, but with punchlines.",
 * "Say goodbye to boring AI chatbot conversations",
 * "The AI Device with the best dad jokes",
 * "The AI wearable for toys"
 *
 */

const HeaderText = "Elato";
interface FrontPageProps {
    user?: User;
    allPersonalities: IPersonality[];
}

// const getRandomPersonalities = (
//     personalities: IPersonality[],
//     count: number
// ) => {
//     return [...personalities].sort(() => Math.random() - 0.5).slice(0, count);
// };

// const getBlobShape = (index: number) => {
//     const shapes = [
//         "40% 60% 70% 30% / 40% 50% 60% 50%",
//         "60% 40% 30% 70% / 60% 30% 50% 40%",
//         "50% 60% 40% 50% / 40% 60% 50% 60%",
//         "30% 70% 60% 40% / 50% 60% 40% 50%",
//         "45% 55% 65% 35% / 55% 45% 55% 45%",
//         "40% 60% 70% 30% / 40% 50% 60% 50%",
//         "60% 40% 30% 70% / 60% 30% 50% 40%",
//         "50% 60% 40% 50% / 40% 60% 50% 60%",
//         "30% 70% 60% 40% / 50% 60% 40% 50%",
//         "45% 55% 65% 35% / 55% 45% 55% 45%",
//         "40% 60% 70% 30% / 40% 50% 60% 50%",
//         "60% 40% 30% 70% / 60% 30% 50% 40%",
//         "50% 60% 40% 50% / 40% 60% 50% 60%",
//         "30% 70% 60% 40% / 50% 60% 40% 50%",
//         "45% 55% 65% 35% / 55% 45% 55% 45%",
//         "40% 60% 70% 30% / 40% 50% 60% 50%",
//         "60% 40% 30% 70% / 60% 30% 50% 40%",
//         "50% 60% 40% 50% / 40% 60% 50% 60%",
//         "30% 70% 60% 40% / 50% 60% 40% 50%",
//         "45% 55% 65% 35% / 55% 45% 55% 45%",
//     ];
//     return shapes[index];
// };

const FrontPage = ({ user }: FrontPageProps) => {
    return (
        <div className="flex flex-col items-center text-center max-w-screen-md px-4 md:px-6 mx-auto justify-center gap-8 mt-10 sm:mt-24">
            <div className="flex flex-col gap-4">
                {/* <Badge
                        className="w-fit flex flex-row gap-2 shadow-md items-center text-sm"
                        variant="secondary"
                    >
                        <Sparkle fill="currentColor" size={12} /> Now Available
                        for Preorder
                    </Badge> */}
                <h1 className="font-semibold tracking-tight text-5xl/tight sm:text-6xl/tight font-silkscreen">
                    {HeaderText}
                </h1>
                {/* <h1 className="mb-4 text-5xl/tight sm:text-6xl/tight font-semibold leading-none tracking-tight dark:text-white">
                    Real time{" "}
                    <span className="underline underline-offset-3 decoration-8 decoration-black">
                        AI conversations
                    </span>{" "}
                    in one compact, open-source device
                </h1> */}
                 <h1 className="text-3xl md:text-5xl font-medium max-w-2xl mx-auto relative z-10 mb-6 leading-[1.2] md:leading-[1.3]">
                    <span className="bg-clip-text font-normal text-transparent bg-gradient-to-r from-amber-500 to-pink-600 drop-shadow-sm">
                    The <span className="font-extrabold"></span>screen-free storytelling toy <span className="font-extrabold">starring your little one</span></span>
                </h1>
                {/* <div className="relative">
                    <h1 className="text-2xl md:text-3xl font-medium text-gray-900 leading-snug max-w-2xl mx-auto px-6 py-4 border-l-4 border-r-4 border-amber-400">
                        {SubHeaderText}
                    </h1>
                </div> */}
                {/* <div className="max-w-4xl text-center mx-8 md:mx-auto flex flex-col gap-4 -mt-4">
                    <AnimatedText />
                </div> */}
            </div>
            {/* <div className="flex flex-col gap-4">
                <DeviceImage />
            </div> */}
        </div>
    );
};

export default FrontPage;

{
    /* <TbArrowWaveRightUp
                        size={64}
                        strokeWidth={1.5}
                        className="absolute bottom-10 left-16 transform -translate-x-1/2 -rotate-30 text-gray-600"
                    />
                    {getRandomPersonalities(allPersonalities, NUM_AVATARS).map(
                        (personality, index) => {
                            // Calculate position on a circle
                            const angle = (index * (2 * Math.PI)) / NUM_AVATARS; // Divide circle by number of avatars
                            const radius = 80; // Radius of the circular arrangement
                            const centerX = 20; // Center point X (from right)
                            const centerY = -10; // Center point Y (from top)

                            // Calculate position using trigonometry
                            const x = Math.cos(angle) * radius;
                            const y = Math.sin(angle) * radius;

                            return (
                                <div
                                    key={personality.key}
                                    className="absolute"
                                    style={{
                                        right: `${centerX + x}px`,
                                        top: `${centerY + y}px`,
                                        // transform: `rotate(${-10 + index * 15}deg)`,
                                        zIndex: 10 - index,
                                    }}
                                >
                                    <div
                                        style={{
                                            borderRadius: "50%",
                                            overflow: "hidden",
                                            width: "80px",
                                            height: "80px",
                                            // border: "2px solid white",
                                            // boxShadow:
                                            //     "0 4px 6px -1px rgb(0 0 0 / 0.1)",
                                            position: "relative",
                                        }}
                                        className="shadow-md"
                                    >
                                        <Image
                                            src={getPersonalityImageSrc(
                                                personality.key
                                            )}
                                            alt={personality.key}
                                            fill
                                            className="object-cover"
                                        />
                                    </div>
                                </div>
                            );
                        }
                    )} */
}
