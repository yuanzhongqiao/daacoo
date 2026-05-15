
"use client";

import { Progress } from "@/components/ui/progress";
import React from "react";
import GeneralUserForm from "../Settings/UserForm";
import { useRouter } from "next/navigation";
import { createClient } from "@/utils/supabase/client";
import { updateUser } from "@/db/users";
import { Loader2 } from "lucide-react";

const Steps: React.FC<{
    selectedUser?: IUser;
    userId: string;
}> = ({ selectedUser, userId }) => {
    const supabase = createClient();
    const router = useRouter();
    const [progress, setProgress] = React.useState(50);
    const [step, setStep] = React.useState(1);

    const onClickFormCallback = async () => {
        setStep(step + 1);
        setProgress(progress + 50);
        router.push("/home");
    };

    const CurrentForm = () => {
        if (step === 1) {
        return (
            <GeneralUserForm
                selectedUser={selectedUser}
                userId={userId}
                onClickCallback={onClickFormCallback}
                onSave={
                    async (values, userType) => {
                        await updateUser(
                            supabase,
                            {
                            supervisee_age: values.supervisee_age,
                            supervisee_name: values.supervisee_name,
                            supervisee_persona: values.supervisee_persona,
                            user_info: {
                                user_type: userType,
                                user_metadata: values,
                            },  
                        },
                        userId);
                }}
                disabled={false}
            />
        );
        } else {
            return <Loader2 className="w-4 h-4 animate-spin" />;
        }
    };

    let heading = "Let's get your Elato device & account set up";
    let subHeading =
        "We want to make sure that your Elato is set up to provide you the best experience possible.";

    if (step === 1) {
        {
            heading = "Hello there!";
            subHeading =
                "With the following details we will be able to personalize your Elato experience.";
        }
    }

    return (
        <div className="max-w-lg flex-auto flex flex-col gap-2 px-1 font-quicksand ">
            <Progress value={progress} className="bg-amber-200" />
            <p className="text-3xl font-bold mt-5">{heading}</p>
            <p className="text-md text-gray-500 font-medium">{subHeading}</p>
            <CurrentForm />
        </div>
    );
};

export default Steps;