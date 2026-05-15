import { Button } from "@/components/ui/button";
import { CheckCircle } from "lucide-react";
import Link from "next/link";


const pricingTiers = [
	{
	  name: "Basic",
	  price: "Free",
	  usage: "60 minutes of usage per month",
	  features: [
		"Basic AI characters",
		"Limited voice interactions"
	  ],
	  popular: false
	},
	{
	  name: "Plus",
	  price: "$10",
	  period: "/month",
	  usage: "60 minutes of usage per day",
	  features: [
		"All AI characters",
		"Enhanced voice quality",
		"Character customization"
	  ],
	  popular: true,
	  link: "https://buy.stripe.com/14k6or0r00j900geV7",
	},
	{
	  name: "Pro",
	  price: "$30",
	  period: "/month",
	  usage: "Unlimited usage",
	  features: [
		"All Plus features",
		"Advanced AI capabilities",
		"Priority support"
	  ],
	  popular: false,
	  link: "https://buy.stripe.com/14k28b6Po7LB14k3cq"
	}
  ];


export const PricingSection = () => {
    return (
			  <div id="pricing" className="flex flex-col md:flex-row items-center justify-center gap-12 md:gap-6 mb-8">
				{pricingTiers.map((tier, index) => (
				  <div key={index} className={`
					${tier.name === 'Basic' ? 'bg-white border-purple-200' : ''}
					${tier.name === 'Plus' ? 'bg-white border-purple-400 transform relative' : ''}
					${tier.name === 'Pro' ? 'bg-white border-purple-500 relative' : ''}
					p-6 rounded-xl shadow-sm w-full md:w-1/3 transition-transform duration-300
				  `}>
					{tier.name === 'Plus' && (
					  <div className="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-yellow-400 text-black text-xs px-3 py-1 rounded-full font-medium">
						Most Popular
					  </div>
					)}
					{tier.name === 'Pro' && (
					  <div className="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-purple-600 text-white text-xs px-3 py-1 rounded-full font-medium">
						Best Value
					  </div>
					)}
					<h3 className={`text-xl font-bold mb-2 ${tier.name === 'Pro' ? 'text-purple-700' : 'text-gray-800'}`}>{tier.name}</h3>
					<div className={`text-3xl font-bold mb-2 ${tier.name === 'Pro' ? 'text-purple-800' : 'text-gray-900'}`}>{tier.price}<span className="text-lg">{tier.period || ''}</span></div>
					<p className="text-sm mb-4 text-gray-600">{tier.usage}</p>
					<ul className="text-left text-sm mb-4 space-y-2 text-gray-700">
					  {tier.features.map((feature, featureIndex) => (
						<li key={featureIndex} className="flex items-center">
						  <CheckCircle className={`h-4 w-4 mr-2 ${tier.name === 'Pro' ? 'text-purple-600' : 'text-purple-500'}`} />
						  <span>{feature}</span>
						</li>
					  ))}
					</ul>
					{tier.name === 'Pro' && (
					  <div className="mt-4 pt-2 border-t border-purple-100">
						<span className="text-xs text-purple-600">Perfect for serious collectors and enthusiasts</span>
					  </div>
					)}
					
					{tier.name === 'Basic' ? (
					  <Button disabled className="w-full mt-4 bg-gray-100 text-gray-500 cursor-not-allowed">
					    Current Plan
					  </Button>
					) : (
					  <Link href={tier.link || ""} passHref>
					    <Button className={`w-full mt-4 ${tier.name === 'Plus' ? 'bg-yellow-400 hover:bg-yellow-500 text-black' : 'bg-purple-600 hover:bg-purple-700 text-white'}`}>
					      Subscribe
					    </Button>
					  </Link>
					)}
				  </div>
				))}
			  </div>
    )
}