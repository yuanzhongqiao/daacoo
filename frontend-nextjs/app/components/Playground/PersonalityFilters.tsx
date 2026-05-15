import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { X } from "lucide-react";
import { Dispatch, SetStateAction } from "react";
import { FaBookOpen, FaHandHoldingMedical } from "react-icons/fa";
import { FaChild } from "react-icons/fa6";

interface PersonalityFiltersProps {
    setSelectedFilters: Dispatch<SetStateAction<PersonalityFilter[]>>;
    selectedFilters: PersonalityFilter[];
    languageState: string;
    currentUser: IUser;
}

const PersonalityFilters = ({
    setSelectedFilters,
    selectedFilters,
    currentUser,
}: PersonalityFiltersProps) => {
    const isDoctor = currentUser.user_info?.user_type === "doctor";

    return (
        <div className="overflow-x-auto scrollbar-hide">
        <ToggleGroup
            type="multiple"
            variant="outline"
            size="sm"
            value={selectedFilters}
            onValueChange={(value: string[]) => {
                setSelectedFilters(value as PersonalityFilter[]);
            }}
            className="justify-start mb-4 ml-1 text-xs inline-flex flex-nowrap min-w-max"
        >
            <ToggleGroupItem
                value="is_story"
                aria-label="Toggle story mode"
                className="rounded-full flex items-center gap-2 text-xs border-purple-200 text-purple-700 hover:bg-purple-50 hover:border-purple-300 hover:shadow-sm hover:shadow-purple-100 transition-all duration-200 [&[data-state=on]]:bg-gradient-to-r [&[data-state=on]]:from-purple-400 [&[data-state=on]]:to-pink-400 [&[data-state=on]]:text-white [&[data-state=on]]:border-transparent [&[data-state=on]]:shadow-lg [&[data-state=on]]:shadow-purple-200 [&[data-state=on]]:animate-pulse"
            >
                <FaBookOpen className="h-4 w-4 text-purple-600" />
                {"Story mode"}
                {selectedFilters.includes("is_story") && (
                    <X className="h-4 w-4" aria-hidden="true" />
                )}
            </ToggleGroupItem>
            <ToggleGroupItem
                value="is_child_voice"
                aria-label="Toggle children filter"
                className="rounded-full flex items-center gap-2 text-xs [&[data-state=on]]:bg-gray-200"
            >
                <FaChild className="h-4 w-4 text-gray-800" />
                {"For children"}
                {selectedFilters.includes("is_child_voice") && (
                    <X className="h-4 w-4" aria-hidden="true" />
                )}
            </ToggleGroupItem>
            {!isDoctor && (
                <ToggleGroupItem
                    value="is_doctor"
                    aria-label="Toggle doctors filter"
                    className="rounded-full flex items-center gap-2 text-xs [&[data-state=on]]:bg-gray-200"
                >
                    <FaHandHoldingMedical className="h-4 w-4 text-gray-800" />
                    {"For caregivers"}
                    {selectedFilters.includes("is_doctor") && (
                        <X className="h-4 w-4" aria-hidden="true" />
                    )}
                </ToggleGroupItem>
            )}
             
        </ToggleGroup>
    </div>
    );
};

export default PersonalityFilters;
