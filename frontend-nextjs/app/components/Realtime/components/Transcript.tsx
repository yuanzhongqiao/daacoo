"use-client";

import React, { useEffect, useRef, useState } from "react";
import { TranscriptItem } from "@/app/components/Realtime/types";
import Image from "next/image";
import { useTranscript } from "@/app/components/Realtime/contexts/TranscriptContext";
import { getPersonalityImageSrc } from "@/lib/utils";
import { ArrowRight } from "lucide-react";
import { dbInsertTranscriptItem } from "@/db/conversations";
import { SupabaseClient } from "@supabase/supabase-js";
import { EmojiComponent } from "../../Playground/EmojiImage";

export interface TranscriptProps {
  userText: string;
  setUserText: (val: string) => void;
  onSendMessage: () => void;
  canSend: boolean;
  personality: IPersonality;
  userId: string;
  isDoctor: boolean;
  supabase: SupabaseClient;
}

function Transcript({
  userText,
  setUserText,
  onSendMessage,
  canSend,
  personality,
  userId,
  isDoctor,
  supabase, 
}: TranscriptProps) {
  const { transcriptItems, toggleTranscriptItemExpand } = useTranscript();
  const transcriptRef = useRef<HTMLDivElement | null>(null);
  const [prevLogs, setPrevLogs] = useState<TranscriptItem[]>([]);
  const [justCopied, setJustCopied] = useState(false);
  const inputRef = useRef<HTMLInputElement | null>(null);

  function scrollToBottom() {
    if (transcriptRef.current) {
      transcriptRef.current.scrollTop = transcriptRef.current.scrollHeight;
    }
  }

  useEffect(() => {
    const hasNewMessage = transcriptItems.length > prevLogs.length;
    const hasUpdatedMessage = transcriptItems.some((newItem, index) => {
      const oldItem = prevLogs[index];
      return (
        oldItem &&
        (newItem.title !== oldItem.title || newItem.data !== oldItem.data)
      );
    });

    if (hasNewMessage || hasUpdatedMessage) {
      scrollToBottom();

      // if (hasNewMessage && transcriptItems.length > 0) {
      //   const newestMessage = transcriptItems[transcriptItems.length - 1];
        
      //   // Do something with the newest message
      //   console.log("New message received:", newestMessage);
        
      //   if (newestMessage.status === "DONE" && newestMessage.type === "MESSAGE") {
      //     dbInsertTranscriptItem(supabase, newestMessage, userId, personality.key, isDoctor);
      //   }
      // }
      
      // If a message was updated, find which one(s)
      if (hasUpdatedMessage) {
        transcriptItems.forEach((newItem, index) => {
          const oldItem = prevLogs[index];
          if (oldItem && (newItem.title !== oldItem.title || newItem.data !== oldItem.data)) {
            console.log("Message updated:", newItem);
            
            if (newItem.type === "MESSAGE" && newItem.role === "user") {
              dbInsertTranscriptItem(supabase, newItem, userId, personality.key, isDoctor);
            }
          }
        });
      }
    }

    setPrevLogs(transcriptItems);
  }, [transcriptItems]);

  // Autofocus on text box input on load
  useEffect(() => {
    if (canSend && inputRef.current) {
      inputRef.current.focus();
    }
  }, [canSend]);

  return (
<div className="flex flex-col h-full bg-white rounded-xl">
      {/* Fixed Personality header */}
      <div className="sticky top-0 p-4 border-b border-gray-200 flex items-center bg-white">
      <div className="w-12 h-12 rounded-full bg-gray-200 overflow-hidden mr-3">
          {personality.key && (
            personality.creator_id === null ? (
              <Image 
                src={getPersonalityImageSrc(personality.key)} 
                alt={personality.title} 
                width={48} 
                height={48} 
                className="object-cover w-full h-full"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center">
                <EmojiComponent personality={personality} size={48} />
              </div>
            )
          )}
        </div>
        <div className="flex-1">
          <h2 className="font-medium text-lg">{personality.title}</h2>
          <p className="text-sm text-gray-500">{personality.subtitle}</p>
        </div>
        {/* <button
          onClick={handleCopyTranscript}
          className="text-sm px-3 py-1.5 rounded-full bg-gray-100 hover:bg-gray-200 text-gray-700"
        >
          {justCopied ? "Copied!" : "Copy"}
        </button> */}
      </div>

    {/* Transcript */}
 <div 
        ref={transcriptRef}
        className="flex-1 overflow-y-auto p-4 flex flex-col gap-y-3"
      >
        {transcriptItems.map((item) => {
          const { itemId, type, role, data, expanded, timestamp, title = "", isHidden } = item;

          if (isHidden) {
            return null;
          }

          if (type === "MESSAGE") {
            const isUser = role === "user";
            const containerClasses = `flex ${isUser ? "justify-end" : "justify-start"} mb-2`;
            const bubbleClasses = `max-w-[80%] p-3 rounded-xl ${
              isUser ? "bg-blue-400 text-white" : "bg-yellow-100 text-gray-800"
            } border-2 ${isUser ? "border-blue-500" : "border-yellow-200"} shadow-sm`;
            const isBracketedMessage = title.startsWith("[") && title.endsWith("]");
            const messageStyle = isBracketedMessage ? "italic text-gray-400 text-md" : "text-md font-medium";
            const displayTitle = isBracketedMessage ? title.slice(1, -1) : title;

            return (
              <div key={itemId} className={containerClasses}>
                <div className={bubbleClasses}>
                  <div className={messageStyle}>{displayTitle}</div>
                  <div className="text-xs opacity-70 mt-1 text-right">
                    {timestamp}
                  </div>
                </div>
              </div>
            );
          }
        })}
      </div>

    {/* 
    <div className="sticky bottom-0 left-0 right-0 p-3 flex items-center gap-x-2 border-t border-gray-200 bg-gray-50 shadow-md">
        <input
          ref={inputRef}
          type="text"
          value={userText}
          onChange={(e) => setUserText(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && canSend) {
              onSendMessage();
            }
          }}
          className="flex-1 px-4 py-2 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          placeholder="Type a message..."
        />
        <button
          onClick={onSendMessage}
          disabled={!canSend || !userText.trim()}
          className="bg-blue-600 text-white rounded-full w-10 h-10 flex items-center justify-center disabled:opacity-50 disabled:bg-gray-400"
        >
          <ArrowRight size={20} />
        </button>
      </div> */}
  </div>
  );
}

export default Transcript;
