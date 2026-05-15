'use client'

import Image from 'next/image';
import { Card, CardContent } from "@/components/ui/card"
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"

const images = [
    "/products/1.png",
    "/products/2.png",
    "/products/3.png",
    "/products/4.png",
]

export default function ProductCarousel() {
  return (
	<Carousel className="w-full mx-auto mb-10">
	<CarouselContent className="-ml-2">
	  {images.map((image, index) => (
		<CarouselItem key={index} className="pl-2 m-4 md:basis-1/4 basis-1/2">
		  <div className="p-1">
			<Card className="shadow-lg rounded-2xl">
			  <CardContent className="flex aspect-square items-center justify-center p-0">
				<div className="relative w-full h-full">
				  <Image 
					src={image} 
					alt={`Product image ${index + 1}`}
					width={300}
					height={300}
					className="object-contain w-auto h-auto max-w-full max-h-full rounded-2xl"
					priority
					onError={(e) => console.error(`Error loading image: ${image}`, e)}
				  />
				</div>
			  </CardContent>
			</Card>
		  </div>
		</CarouselItem>
	  ))}
	</CarouselContent>
	<CarouselPrevious className="hidden" />
	<CarouselNext className="hidden" />
  </Carousel>
  )
}