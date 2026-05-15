import { SupabaseClient } from "@supabase/supabase-js";
import { TranscriptItem } from "@/app/components/Realtime/types";

export const dbInsertTranscriptItem = async (
    supabase: SupabaseClient,
    data: TranscriptItem,
    userId: string,
    personalityKey: string,
    isDoctor: boolean
) => {
    const role = data.role == "user" ? isDoctor ? "doctor" : "user" : "assistant";

    const conversation: IConversation = {
        user_id: userId,
        personality_key: personalityKey,
        role: role,
        content: data.title ?? "",
        is_sensitive: false,
        metadata: null,
        chat_group_id: null,
    };

    await dbInsertConversation(supabase, conversation);
};

export const dbInsertConversation = async (
    supabase: SupabaseClient,
    data: IConversation
) => {
    const { error } = await supabase.from("conversations").insert([data]);
    if (error) {
        throw error;
    }
};

export const dbGetConversation = async (
    supabase: SupabaseClient,
    userId: string
) => {
    // Get the date 7 days ago from today
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

    const sevenDaysAgoISO = sevenDaysAgo.toISOString();

    const { data, error } = await supabase
        .from("conversations")
        .select("*")
        .eq("user_id", userId)
        .eq("role", "user")
        .gte("created_at", sevenDaysAgoISO);
    // .order("created_at", { ascending: true });

    if (error) {
        throw error;
    }
    return data;
};

export const dbGetRecentMessages = async (
    supabase: SupabaseClient,
    userId: string,
    toyId: string,
    personalityId: string
) => {
    const { data, error } = await supabase
        .from("conversations")
        .select("*")
        .eq("user_id", userId)
        .eq("toy_id", toyId)
        .eq("personality_id", personalityId)
        .order("created_at", { ascending: false })
        .limit(20);

    if (error) {
        throw error;
    }
    return data;
};
