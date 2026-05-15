import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import { defaultPersonalityId } from "./data";

export const getOpenGraphMetadata = (title: string) => {
    return {
        openGraph: {
            title: `${title} | Elato AI`,
        },
    };
};

export const PitchFactors = [
    { emoji: "🧟‍♂️", label: "Super Deep", desc: "Like Hulk" },
    { emoji: "👤", label: "Normal", desc: "Regular voice" },
    { emoji: "👧", label: "Higher", desc: "Kid-like voice" },
    { emoji: "🐿️", label: "Squeaky", desc: "Like Alvin" },
];

// code in the form: aabbccddeeff
export const isValidMacAddress = (macAddress: string): boolean => {
    // Check if macAddress is a valid MAC address with colon separators
    const macRegex = /^([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2})$/;
    return macRegex.test(macAddress);
};

export const getMacAddressFromDeviceCode = (deviceCode: string): string => {
    // add colons to the device code
    return deviceCode.substring(0, 2) + ":" + deviceCode.substring(2, 4) + ":" +
        deviceCode.substring(4, 6) + ":" + deviceCode.substring(6, 8) + ":" +
        deviceCode.substring(8, 10) + ":" + deviceCode.substring(10, 12);
};

export const getPersonalityImageSrc = (title: string) => {
    return `/personality/${title.toLowerCase().replace(/\s+/g, "_")}.jpeg`;
};

export function removeEmojis(text: string): string {
    const emojiPattern: RegExp =
        /[\u{1F600}-\u{1F64F}\u{1F300}-\u{1F5FF}\u{1F680}-\u{1F6FF}\u{1F1E0}-\u{1F1FF}\u{2702}-\u{27B0}\u{24C2}-\u{1F251}]/gu;
    return text.replace(emojiPattern, "");
}

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export const isDefaultPersonality = (personality: IPersonality) => {
    return personality.personality_id === defaultPersonalityId;
};

export const getBaseUrl = () => {
    return process.env.NEXT_PUBLIC_VERCEL_ENV === "production"
        ? "https://elatoai.com"
        : "http://localhost:3000";
};

export const getUserAvatar = (avatar_url: string) => {
    return avatar_url;
};

export const getAssistantAvatar = (imageSrc: string) => {
    return "/" + imageSrc + ".png";
};
