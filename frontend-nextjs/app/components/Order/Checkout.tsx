import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ShoppingCart } from "lucide-react";
import { DEVICE_COST, ORIGINAL_COST, paymentLink } from "@/lib/data";
import { useToast } from "@/components/ui/use-toast";
import Link from "next/link";

interface CheckoutProps {
    deviceCost: number;
    originalCost: number;
    paymentLink: string;
}

const Checkout = ({ deviceCost, originalCost, paymentLink }: CheckoutProps) => {

    const totalSavings = originalCost - deviceCost;

    const freeShipping = deviceCost >= 100;

    return (
        <div className="flex flex-col gap-6">
            <div className="flex items-center gap-4 flex-wrap">
                <div className="text-3xl font-semibold">${deviceCost}</div>
                <div className="text-lg font-medium text-gray-400 line-through">
                    ${originalCost}
                </div>
                <Badge variant="secondary" className="font-medium rounded-md">
                    Save ${totalSavings.toFixed(0)}
                </Badge>
            </div>
            {freeShipping && (
                    <p className="text-sm text-gray-400">FREE Shipping</p>
                )}
            <div className="flex items-center gap-4 mb-6">
                <Link href={paymentLink}>
                <Button
                    size="sm"
                    // className="w-full h-10 rounded-full"
                    className="w-full rounded-full sm:w-auto flex-row items-center gap-2 text-white border-0 text-sm px-4 py-2"

                    // variant="upsell_primary"
                    // onClick={handleCheckout}
                >
                    <ShoppingCart className="mr-2 h-5 w-5" />
                    Buy Now
                </Button>
                </Link>
            </div>
        </div>
    );
};

export default Checkout;
