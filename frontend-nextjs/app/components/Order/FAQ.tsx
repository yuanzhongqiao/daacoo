import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
} from "@/components/ui/accordion";
import { cn } from "@/lib/utils";


export interface qnaProps {
    question: string;
    answer: string;
    icon: React.ReactNode;
}


const FAQ = ({
    qna,
    className,
    titleClassName,
}: {
    qna: qnaProps[];
    className?: string;
    titleClassName?: string;
}) => {
    return (
        <div className={cn("mb-16 px-4 max-w-screen-sm w-full mx-auto", className)}>
            <h2 className={cn("text-4xl font-semibold mb-8 text-center", titleClassName)}>
                Frequently Asked Questions
            </h2>
            <Accordion type="single" collapsible className="w-full">
                {qna.map((faq, index) => (
                    <AccordionItem
                        key={index}
                        value={`item-${index}`}
                        className="w-full"
                    >
                        <AccordionTrigger className="flex items-center justify-between">
                            <div className="flex gap-6 text-left">
                                <div className="w-6 h-6 flex-shrink-0">
                                    {faq.icon}
                                </div>
                                <span>{faq.question}</span>
                            </div>
                        </AccordionTrigger>
                        <AccordionContent className="w-full">
                            {faq.answer}
                        </AccordionContent>
                    </AccordionItem>
                ))}
            </Accordion>
        </div>
    );
};

export default FAQ;
