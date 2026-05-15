"use client";

import { useEffect, useState } from "react";
import NavbarButtons from "./NavbarButtons";
import { useMediaQuery } from "@/hooks/useMediaQuery";
import { usePathname } from "next/navigation";
import LeftNavbarButtons from "./LeftNavbarButtons";

export function Navbar({
    user,
    stars,
}: {
    user: IUser | null;
    stars: number | null;
}) {
    const [isVisible, setIsVisible] = useState(true);
    const [lastScrollY, setLastScrollY] = useState(0);
    const isMobile = useMediaQuery("(max-width: 768px)");
    const isHome = usePathname().includes("/home");
    const isProduct = usePathname().includes("/products");

    useEffect(() => {
        if (typeof window !== "undefined" && isMobile) {
            const handleScroll = () => {
                const currentScrollY = window.scrollY;
                setIsVisible(
                    currentScrollY <= 0 || currentScrollY < lastScrollY
                );
                setLastScrollY(currentScrollY);
            };

            window.addEventListener("scroll", handleScroll, { passive: true });
            return () => window.removeEventListener("scroll", handleScroll);
        }
    }, [lastScrollY, isMobile]);

    return (
        <div
            className={`backdrop-blur-[3px] flex-none flex items-center sticky top-0 z-50 transition-transform duration-300 h-[60px] ${
                isVisible ? "translate-y-0" : "-translate-y-full"
            }`}
        >
            <nav
                className={`mx-auto w-full max-w-[1440px] px-4 flex items-center justify-between`}
            >
                <LeftNavbarButtons user={user} />
                <NavbarButtons user={user} stars={stars} isHome={isHome} />
            </nav>
        </div>
    );
}
