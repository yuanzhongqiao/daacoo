import { Check } from "lucide-react";

const Specs = ({ includedItems, technicalSpecs }: { includedItems: string[], technicalSpecs: string[] }) => {
    const CheckIcon = <Check className="h-5 w-5 text-primary flex-shrink-0" />;
    return (
        <div className="mb-16">
            <h2 className="text-4xl font-semibold mb-8 text-center">
                Product Details
            </h2>
            <div className="grid md:grid-cols-2 gap-8">
                <div>
                    <h3 className="text-xl font-semibold mb-4">
                        What&apos;s Included:
                    </h3>
                    <ul className="space-y-2">
                        {includedItems.map((item, index) => (
                            <li className="flex items-center gap-2" key={index}>
                                {CheckIcon}
                                <span>{item}</span>
                            </li>
                        ))}
                    </ul>
                </div>
                <div>
                    <h3 className="text-xl font-semibold mb-4">
                        Technical Specs:
                    </h3>
                    <ul className="space-y-2">
                        {technicalSpecs.map((spec, index) => (
                            <li className="flex items-center gap-2" key={index}>
                                {CheckIcon}
                                <span>{spec}</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default Specs;
