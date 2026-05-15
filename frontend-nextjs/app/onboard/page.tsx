import { createClient } from "@/utils/supabase/server";
import Steps from "../components/Onboarding/Steps";

export default async function Home() {
    const supabase = createClient();

    const {
        data: { user },
    } = await supabase.auth.getUser();

    return (
        <div className="flex flex-col gap-2">
            <Steps userId={user?.id ?? ""} />
        </div>
    );
}