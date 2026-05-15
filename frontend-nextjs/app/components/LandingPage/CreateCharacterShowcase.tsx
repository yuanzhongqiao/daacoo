import { VoiceSettings } from "./VoiceSettings"

export const CreateCharacterShowcase = () => {
    return (
        <section className="py-16 bg-gradient-to-b from-gray-50 to-white">
      <div className="container mx-auto px-4 max-w-screen-lg">
        <div className="flex flex-col lg:flex-row-reverse items-center gap-12">
			          {/* Text Content - On right for desktop, top for mobile */}
					  <div className="order-1 lg:order-2 w-full lg:w-2/5">
            <h2 className="text-3xl md:text-4xl font-bold mb-6 text-gray-800">
              Create Your Own AI
            </h2>
			<p className="text-lg text-gray-600 mb-6">
              Create a character that is unique and personalized to your needs.
            </p>
          </div>
         {/* Character List - On left for desktop, bottom for mobile */}
		 <div className="order-2 lg:order-1 w-full lg:w-3/5 sm:max-w-[400px] mx-auto">
            <div className="mx-auto px-2 rounded-lg">
			<VoiceSettings />
            </div>
          </div>
        </div>
      </div>
    </section>
    )
}