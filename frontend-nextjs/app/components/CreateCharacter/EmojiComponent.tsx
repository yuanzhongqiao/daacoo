import Twemoji from "react-twemoji";

const EmojiComponent = ({ emoji }: { emoji: string | undefined }) => {
    return (
        <div className="w-7 h-7 flex items-center justify-center">
            <Twemoji
                options={{ className: "twemoji w-7 h-7 flex-shrink-0" }}
            >
                {emoji}
            </Twemoji>
        </div>
    );
};

export default EmojiComponent;