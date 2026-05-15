

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;


-- CREATE EXTENSION IF NOT EXISTS "pgsodium" WITH SCHEMA "pgsodium";






COMMENT ON SCHEMA "public" IS 'standard public schema';



CREATE EXTENSION IF NOT EXISTS "pg_graphql" WITH SCHEMA "graphql";






CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "pgjwt" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";






CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "vector" WITH SCHEMA "public";






CREATE TYPE "public"."tts_model_enum" AS ENUM (
    'FISH',
    'AZURE'
);


ALTER TYPE "public"."tts_model_enum" OWNER TO "postgres";


CREATE OR REPLACE FUNCTION "public"."match_documents"("query_embedding" "public"."vector", "match_count" integer DEFAULT NULL::integer, "filter" "jsonb" DEFAULT '{}'::"jsonb") RETURNS TABLE("id" "uuid", "content" "text", "metadata" "jsonb", "embedding" "public"."vector", "similarity" double precision)
    LANGUAGE "plpgsql"
    AS $$
#variable_conflict use_column
begin
  return query
  select
    id,
    content,
    metadata,
    embedding,
    1 - (arxiv_embeddings.embedding <=> query_embedding) as similarity
  from arxiv_embeddings
  where metadata @> filter
  order by arxiv_embeddings.embedding <=> query_embedding
  limit match_count;
end;
$$;


ALTER FUNCTION "public"."match_documents"("query_embedding" "public"."vector", "match_count" integer, "filter" "jsonb") OWNER TO "postgres";

SET default_tablespace = '';

SET default_table_access_method = "heap";


CREATE TABLE IF NOT EXISTS "public"."api_keys" (
    "api_key_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "user_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "encrypted_key" "text" NOT NULL,
    "iv" "text" NOT NULL
);


ALTER TABLE "public"."api_keys" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "public"."conversations" (
    "conversation_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "user_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "role" "text" NOT NULL,
    "content" "text" NOT NULL,
    "metadata" "jsonb",
    "chat_group_id" "uuid",
    "is_sensitive" boolean DEFAULT false,
    "personality_key" "text"
);


ALTER TABLE "public"."conversations" OWNER TO "postgres";


COMMENT ON TABLE "public"."conversations" IS 'conversations table';



CREATE TABLE IF NOT EXISTS "public"."devices" (
    "device_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "mac_address" "text",
    "user_code" "text" NOT NULL,
    "user_id" "uuid",
    "is_ota" boolean DEFAULT false NOT NULL,
    "is_reset" boolean DEFAULT false NOT NULL,
    "volume" smallint DEFAULT '70'::smallint NOT NULL
);


ALTER TABLE "public"."devices" OWNER TO "postgres";


COMMENT ON TABLE "public"."devices" IS 'all deployed devices';



COMMENT ON COLUMN "public"."devices"."is_ota" IS 'air update device';



COMMENT ON COLUMN "public"."devices"."is_reset" IS 'factory reset device';



COMMENT ON COLUMN "public"."devices"."volume" IS 'device volume';


CREATE TABLE IF NOT EXISTS "public"."languages" (
    "language_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "code" "text" NOT NULL,
    "name" "text" NOT NULL,
    "flag" "text" NOT NULL
);


ALTER TABLE "public"."languages" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "public"."personalities" (
    "personality_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "is_doctor" boolean DEFAULT false NOT NULL,
    "key" "text" DEFAULT ''::"text" NOT NULL,
    "is_child_voice" boolean DEFAULT false,
    "oai_voice" "text" DEFAULT 'ash'::"text" NOT NULL,
    "voice_prompt" "text" DEFAULT 'the voice should sound smooth and nice to hear'::"text" NOT NULL,
    "title" "text" DEFAULT ''::"text" NOT NULL,
    "subtitle" "text" DEFAULT ''::"text" NOT NULL,
    "short_description" "text" DEFAULT ''::"text" NOT NULL,
    "character_prompt" "text" DEFAULT ''::"text" NOT NULL,
    "creator_id" "uuid",
    "is_story" boolean DEFAULT false NOT NULL
);


ALTER TABLE "public"."personalities" OWNER TO "postgres";


COMMENT ON TABLE "public"."personalities" IS 'describes character personalities';



COMMENT ON COLUMN "public"."personalities"."voice_prompt" IS 'describe what the voice should sound like';



COMMENT ON COLUMN "public"."personalities"."is_story" IS 'can tell stories';



CREATE TABLE IF NOT EXISTS "public"."users" (
    "user_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "supervisor_name" "text" NOT NULL,
    "supervisee_name" "text" NOT NULL,
    "supervisee_persona" "text" DEFAULT ''::"text" NOT NULL,
    "supervisee_age" smallint DEFAULT '3'::smallint NOT NULL,
    "email" "text" DEFAULT ''::"text" NOT NULL,
    "session_time" integer DEFAULT 0 NOT NULL,
    "avatar_url" "text" DEFAULT ''::"text" NOT NULL,
    "personality_id" "uuid" DEFAULT 'a1c073e6-653d-40cf-acc1-891331689409'::"uuid" NOT NULL,
    "is_premium" boolean DEFAULT false NOT NULL,
    "user_info" "jsonb" DEFAULT '{"user_type": "user", "user_metadata": {}}'::"jsonb" NOT NULL,
    "language_code" "text" DEFAULT 'en-US'::"text" NOT NULL,
    "device_id" "uuid",
    CONSTRAINT "users_child_age_check" CHECK (("supervisee_age" > 1))
);


ALTER TABLE "public"."users" OWNER TO "postgres";


COMMENT ON TABLE "public"."users" IS 'users table';



COMMENT ON COLUMN "public"."users"."session_time" IS 'number of seconds talked';



COMMENT ON COLUMN "public"."users"."avatar_url" IS 'oauth avatar';



COMMENT ON COLUMN "public"."users"."personality_id" IS 'FK to personality';



COMMENT ON COLUMN "public"."users"."is_premium" IS 'is premium subscriber';



COMMENT ON COLUMN "public"."users"."user_info" IS 'user_info and metadata';



COMMENT ON COLUMN "public"."users"."device_id" IS 'links to the device a user owns';



ALTER TABLE ONLY "public"."api_keys"
    ADD CONSTRAINT "api_keys_pkey" PRIMARY KEY ("api_key_id");



ALTER TABLE ONLY "public"."api_keys"
    ADD CONSTRAINT "api_keys_user_id_key" UNIQUE ("user_id");



ALTER TABLE ONLY "public"."conversations"
    ADD CONSTRAINT "conversations_pkey" PRIMARY KEY ("conversation_id");



ALTER TABLE ONLY "public"."devices"
    ADD CONSTRAINT "devices_mac_address_key" UNIQUE ("mac_address");



ALTER TABLE ONLY "public"."devices"
    ADD CONSTRAINT "devices_pkey" PRIMARY KEY ("device_id");



ALTER TABLE ONLY "public"."devices"
    ADD CONSTRAINT "devices_user_code_key" UNIQUE ("user_code");

ALTER TABLE ONLY "public"."languages"
    ADD CONSTRAINT "languages_language_key" UNIQUE ("code");



ALTER TABLE ONLY "public"."languages"
    ADD CONSTRAINT "languages_pkey" PRIMARY KEY ("language_id");



ALTER TABLE ONLY "public"."personalities"
    ADD CONSTRAINT "personalities_key_key" UNIQUE ("key");



ALTER TABLE ONLY "public"."personalities"
    ADD CONSTRAINT "personalities_pkey" PRIMARY KEY ("personality_id");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_device_id_key" UNIQUE ("device_id");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pkey" PRIMARY KEY ("user_id");



ALTER TABLE ONLY "public"."api_keys"
    ADD CONSTRAINT "api_keys_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("user_id") ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY "public"."conversations"
    ADD CONSTRAINT "conversations_personality_key_fkey" FOREIGN KEY ("personality_key") REFERENCES "public"."personalities"("key") ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY "public"."conversations"
    ADD CONSTRAINT "conversations_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("user_id");



ALTER TABLE ONLY "public"."devices"
    ADD CONSTRAINT "devices_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."users"("user_id") ON UPDATE CASCADE ON DELETE SET NULL;



ALTER TABLE ONLY "public"."personalities"
    ADD CONSTRAINT "personalities_creator_id_fkey" FOREIGN KEY ("creator_id") REFERENCES "public"."users"("user_id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_device_id_fkey" FOREIGN KEY ("device_id") REFERENCES "public"."devices"("device_id") ON UPDATE CASCADE ON DELETE SET NULL;



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_language_code_fkey" FOREIGN KEY ("language_code") REFERENCES "public"."languages"("code") ON UPDATE CASCADE ON DELETE SET DEFAULT;



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_personality_id_fkey" FOREIGN KEY ("personality_id") REFERENCES "public"."personalities"("personality_id") ON DELETE SET DEFAULT;



CREATE POLICY "Enable delete for users based on user_id" ON "public"."api_keys" FOR DELETE TO "authenticated" USING ((( SELECT "auth"."uid"() AS "uid") = "user_id"));



CREATE POLICY "Enable insert for authenticated users only" ON "public"."api_keys" FOR INSERT TO "authenticated" WITH CHECK (true);



CREATE POLICY "Enable insert for authenticated users only" ON "public"."devices" FOR INSERT TO "authenticated" WITH CHECK (true);



CREATE POLICY "Enable insert for authenticated users only" ON "public"."users" FOR INSERT TO "authenticated" WITH CHECK (true);



CREATE POLICY "Enable read access for all users" ON "public"."devices" FOR SELECT USING (true);


CREATE POLICY "Enable read access for all users" ON "public"."languages" FOR SELECT USING (true);



CREATE POLICY "Enable read access for all users" ON "public"."personalities" FOR SELECT USING (true);



CREATE POLICY "Enable read access for all users" ON "public"."users" FOR SELECT USING (true);



CREATE POLICY "Enable update for users based on email" ON "public"."users" FOR UPDATE USING (((( SELECT "auth"."jwt"() AS "jwt") ->> 'email'::"text") = "email")) WITH CHECK (((( SELECT "auth"."jwt"() AS "jwt") ->> 'email'::"text") = "email"));



CREATE POLICY "Enable users to view their own data only" ON "public"."api_keys" FOR SELECT TO "authenticated" USING ((( SELECT "auth"."uid"() AS "uid") = "user_id"));



CREATE POLICY "PUBLIC conversations" ON "public"."conversations" USING (true);



CREATE POLICY "Update user table" ON "public"."users" FOR UPDATE TO "anon" USING (true) WITH CHECK (true);



ALTER TABLE "public"."api_keys" ENABLE ROW LEVEL SECURITY;


ALTER TABLE "public"."conversations" ENABLE ROW LEVEL SECURITY;


ALTER TABLE "public"."devices" ENABLE ROW LEVEL SECURITY;


CREATE POLICY "esp users can update devices" ON "public"."devices" FOR UPDATE USING (true) WITH CHECK (true);

ALTER TABLE "public"."languages" ENABLE ROW LEVEL SECURITY;


CREATE POLICY "only insert if you're auth'd" ON "public"."personalities" FOR INSERT TO "authenticated" WITH CHECK (true);



ALTER TABLE "public"."personalities" ENABLE ROW LEVEL SECURITY;


ALTER TABLE "public"."users" ENABLE ROW LEVEL SECURITY;




ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";


GRANT USAGE ON SCHEMA "public" TO "postgres";
GRANT USAGE ON SCHEMA "public" TO "anon";
GRANT USAGE ON SCHEMA "public" TO "authenticated";
GRANT USAGE ON SCHEMA "public" TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_in"("cstring", "oid", integer) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_in"("cstring", "oid", integer) TO "anon";
GRANT ALL ON FUNCTION "public"."vector_in"("cstring", "oid", integer) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_in"("cstring", "oid", integer) TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_out"("public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_out"("public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_out"("public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_out"("public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_recv"("internal", "oid", integer) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_recv"("internal", "oid", integer) TO "anon";
GRANT ALL ON FUNCTION "public"."vector_recv"("internal", "oid", integer) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_recv"("internal", "oid", integer) TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_send"("public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_send"("public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_send"("public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_send"("public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_typmod_in"("cstring"[]) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_typmod_in"("cstring"[]) TO "anon";
GRANT ALL ON FUNCTION "public"."vector_typmod_in"("cstring"[]) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_typmod_in"("cstring"[]) TO "service_role";



GRANT ALL ON FUNCTION "public"."array_to_vector"(real[], integer, boolean) TO "postgres";
GRANT ALL ON FUNCTION "public"."array_to_vector"(real[], integer, boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."array_to_vector"(real[], integer, boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."array_to_vector"(real[], integer, boolean) TO "service_role";



GRANT ALL ON FUNCTION "public"."array_to_vector"(double precision[], integer, boolean) TO "postgres";
GRANT ALL ON FUNCTION "public"."array_to_vector"(double precision[], integer, boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."array_to_vector"(double precision[], integer, boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."array_to_vector"(double precision[], integer, boolean) TO "service_role";



GRANT ALL ON FUNCTION "public"."array_to_vector"(integer[], integer, boolean) TO "postgres";
GRANT ALL ON FUNCTION "public"."array_to_vector"(integer[], integer, boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."array_to_vector"(integer[], integer, boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."array_to_vector"(integer[], integer, boolean) TO "service_role";



GRANT ALL ON FUNCTION "public"."array_to_vector"(numeric[], integer, boolean) TO "postgres";
GRANT ALL ON FUNCTION "public"."array_to_vector"(numeric[], integer, boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."array_to_vector"(numeric[], integer, boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."array_to_vector"(numeric[], integer, boolean) TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_to_float4"("public"."vector", integer, boolean) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_to_float4"("public"."vector", integer, boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."vector_to_float4"("public"."vector", integer, boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_to_float4"("public"."vector", integer, boolean) TO "service_role";



GRANT ALL ON FUNCTION "public"."vector"("public"."vector", integer, boolean) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector"("public"."vector", integer, boolean) TO "anon";
GRANT ALL ON FUNCTION "public"."vector"("public"."vector", integer, boolean) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector"("public"."vector", integer, boolean) TO "service_role";


GRANT ALL ON FUNCTION "public"."cosine_distance"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."cosine_distance"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."cosine_distance"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."cosine_distance"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."hnswhandler"("internal") TO "postgres";
GRANT ALL ON FUNCTION "public"."hnswhandler"("internal") TO "anon";
GRANT ALL ON FUNCTION "public"."hnswhandler"("internal") TO "authenticated";
GRANT ALL ON FUNCTION "public"."hnswhandler"("internal") TO "service_role";



GRANT ALL ON FUNCTION "public"."inner_product"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."inner_product"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."inner_product"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."inner_product"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."ivfflathandler"("internal") TO "postgres";
GRANT ALL ON FUNCTION "public"."ivfflathandler"("internal") TO "anon";
GRANT ALL ON FUNCTION "public"."ivfflathandler"("internal") TO "authenticated";
GRANT ALL ON FUNCTION "public"."ivfflathandler"("internal") TO "service_role";



GRANT ALL ON FUNCTION "public"."l1_distance"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."l1_distance"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."l1_distance"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."l1_distance"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."l2_distance"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."l2_distance"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."l2_distance"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."l2_distance"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."match_documents"("query_embedding" "public"."vector", "match_count" integer, "filter" "jsonb") TO "anon";
GRANT ALL ON FUNCTION "public"."match_documents"("query_embedding" "public"."vector", "match_count" integer, "filter" "jsonb") TO "authenticated";
GRANT ALL ON FUNCTION "public"."match_documents"("query_embedding" "public"."vector", "match_count" integer, "filter" "jsonb") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_accum"(double precision[], "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_accum"(double precision[], "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_accum"(double precision[], "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_accum"(double precision[], "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_add"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_add"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_add"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_add"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_avg"(double precision[]) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_avg"(double precision[]) TO "anon";
GRANT ALL ON FUNCTION "public"."vector_avg"(double precision[]) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_avg"(double precision[]) TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_cmp"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_cmp"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_cmp"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_cmp"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_combine"(double precision[], double precision[]) TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_combine"(double precision[], double precision[]) TO "anon";
GRANT ALL ON FUNCTION "public"."vector_combine"(double precision[], double precision[]) TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_combine"(double precision[], double precision[]) TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_dims"("public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_dims"("public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_dims"("public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_dims"("public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_eq"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_eq"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_eq"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_eq"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_ge"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_ge"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_ge"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_ge"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_gt"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_gt"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_gt"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_gt"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_l2_squared_distance"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_l2_squared_distance"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_l2_squared_distance"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_l2_squared_distance"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_le"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_le"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_le"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_le"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_lt"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_lt"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_lt"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_lt"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_mul"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_mul"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_mul"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_mul"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_ne"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_ne"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_ne"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_ne"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_negative_inner_product"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_negative_inner_product"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_negative_inner_product"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_negative_inner_product"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_norm"("public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_norm"("public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_norm"("public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_norm"("public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_spherical_distance"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_spherical_distance"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_spherical_distance"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_spherical_distance"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."vector_sub"("public"."vector", "public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."vector_sub"("public"."vector", "public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."vector_sub"("public"."vector", "public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."vector_sub"("public"."vector", "public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."avg"("public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."avg"("public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."avg"("public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."avg"("public"."vector") TO "service_role";



GRANT ALL ON FUNCTION "public"."sum"("public"."vector") TO "postgres";
GRANT ALL ON FUNCTION "public"."sum"("public"."vector") TO "anon";
GRANT ALL ON FUNCTION "public"."sum"("public"."vector") TO "authenticated";
GRANT ALL ON FUNCTION "public"."sum"("public"."vector") TO "service_role";

GRANT ALL ON TABLE "public"."api_keys" TO "anon";
GRANT ALL ON TABLE "public"."api_keys" TO "authenticated";
GRANT ALL ON TABLE "public"."api_keys" TO "service_role";



GRANT ALL ON TABLE "public"."conversations" TO "anon";
GRANT ALL ON TABLE "public"."conversations" TO "authenticated";
GRANT ALL ON TABLE "public"."conversations" TO "service_role";



GRANT ALL ON TABLE "public"."devices" TO "anon";
GRANT ALL ON TABLE "public"."devices" TO "authenticated";
GRANT ALL ON TABLE "public"."devices" TO "service_role";

GRANT ALL ON TABLE "public"."languages" TO "anon";
GRANT ALL ON TABLE "public"."languages" TO "authenticated";
GRANT ALL ON TABLE "public"."languages" TO "service_role";



GRANT ALL ON TABLE "public"."personalities" TO "anon";
GRANT ALL ON TABLE "public"."personalities" TO "authenticated";
GRANT ALL ON TABLE "public"."personalities" TO "service_role";



GRANT ALL ON TABLE "public"."users" TO "anon";
GRANT ALL ON TABLE "public"."users" TO "authenticated";
GRANT ALL ON TABLE "public"."users" TO "service_role";



ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "service_role";






























RESET ALL;
