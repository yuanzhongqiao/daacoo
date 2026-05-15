"use client"

import Twemoji from "react-twemoji";

export const Emoji = ({ emoji }: { emoji: string }) => {
	return (
		<div className="flex flex-row items-center justify-center w-10 h-10">
			<Twemoji options={{ 
				className: "twemoji flex-shrink-0",
				style: { fontSize: `${10}px` }
			}}>
				{emoji}
			</Twemoji>
		</div>
	)
}