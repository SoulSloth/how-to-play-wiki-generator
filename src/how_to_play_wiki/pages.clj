(ns how-to-play-wiki.pages
  (:require [stasis.core :as stasis]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]
            [optimus.link :as link]
            [markdown.core :as md]
            [clojure.string :as str]
            [markdown.core :as m]))

(defn home-page
  "Home page"
  [page]
  (html5
   [:div
    [:h2 "How to Play Wiki"]]))

(defn directory-page
  "List of items in a category"
  [{:keys [item-category blurb]} pages]
  [:div
   [:h1 (str/capitalize (str/replace (str item-category) #":" ""))]
   [:hr]
   [:p blurb]
   [:hr]
   [:ul
    (for [page
          ;;Grab all the pages in the enemy category
          (filter #(re-find (re-pattern (str ":category " item-category)) (second %)) pages)]
      (let [{title :title category :category} (read-string (second page))]
        [:li [:a {:href (str/replace (first page) #"\.edn$" "/")} title]]))]])

(defn stat-table
  "Create a table to display stats"
  [stats request stat-order]
  [:table.crunch
   [:tr
    (for [name stat-order]
      [:th
       [:img {:src (link/file-path request (str "/assets/stats/" name ".webp")) :alt name :title name}]])]
   [:tr
    (for [stat stat-order]
      [:td (get stats (keyword stat))])]])

(defn character-stat-table
  [stats request]
  (stat-table stats request
              ["level" "vitality" "attunement" "endurance" "strength" "dexterity" "resistance" "intelligence" "faith" "humanity"]))

(defn defense-stat-table
  [stats request]
  (stat-table (get stats :defense) request
              ["physical" "strike" "slash" "thrust" "magic" "fire" "lightning"]))

(defn resistance-stat-table
  [stats request]
  (stat-table (get stats :resistance) request
              ["bleed" "poison" "curse"]))

(defn armor-stat-table
  [{:keys [poise durability weight]} request]
  (stat-table {:poise poise :durability durability :weight weight}
              request
              ["poise" "durability" "weight"]))

(defn block-quote
  "Create a block quote"
  [blub & author]
  [:div.markdown
   [:blockquote
    [:p blub]
    (if author
      [:footer author])]])

(defn class-page
  "display information about a class"
  [{:keys [title stats image md-page]} request]
  (html5
   [:div
    [:h2 title]
    [:img {:src (link/file-path request (str "/assets/classes/" title ".webp"))}]
    (if md-page
      ;;TODO:Disgusting tech debt to support markdown
      ;;Maybe just make resource-dir part of the env?
      [:div.markdown (m/md-to-html-string (slurp (str "site-content/edn/classes/" title ".md")))])
    (character-stat-table stats request)]))

(defn armor-page
  [{:keys [image stats locations upgrades blurb notes]} request]
  [:div
   (block-quote blurb "In-game description")
   [:img {:src (link/file-path request (str "/assets/armors/" image))}]
   (defense-stat-table stats request)
   (resistance-stat-table stats request)
   (armor-stat-table stats request)
   [:h2 "Locations"]
   [:ul
    (for [{:keys [name link]} locations]
      [:li [:a (if link {:href link}) name]])]
   [:h2 "Notes"]
   [:div.markdown (m/md-to-html-string (slurp (str "site-content/edn/armors/" notes)))]
   [:h2 "Upgrades"]
   (let
    [stat-order ["level" "physical" "strike" "slash" "thrust" "magic" "fire" "lightning" "bleed" "poison" "curse"]]
     [:table
      [:tr
       (for [stat stat-order]
         [:th
          [:img {:src (link/file-path request (str "/assets/stats/" stat ".webp")) :alt stat :title stat}]])]
      (for [upgrade upgrades]
        (let
         [flat-upgrade-stat (conj (get upgrade :defense) (get upgrade :resistance) (first upgrade))]
          [:tr
           (for [stat stat-order]
             [:td (get flat-upgrade-stat (keyword stat))])]))])])

(defn enemy-page
  "layout an enemy page"
  [{:keys [title category description location drops portrait]}]
  (html5
   [:div
    [:h1 title]
    [:hr]
    [:aside.profile
     [:h2 title]
     [:img {:src portrait}]
     [:h3 "Defense Attributes"]
     [:hr]
     (let [defense (:defense (first location))
           headers (keys defense)
           values (vals defense)]
       [:table.crunch
        [:tr
         ;;TODO: dangerously coming close to hashtable_threshold....
         (for [[header value] defense]
           [:th
            [:a header]])]
        [:tr
         (for [[header value] defense]
           [:td
            [:a value]])]])
     [:h3 "Resistances"]
     [:hr]
     (let [resist (:resistance (first location))
           headers (keys resist)
           values (vals resist)]
       [:table.crunch
        [:tr
         (for [header headers]
           [:th
            [:a header]])]
        [:tr
         (for [value values]
           [:td
            [:a value]])]])]
;;TODO: Markdown goes here
    [:div description]
    [:h2 "Location"]
    [:div
     [:ul (for [{:keys [name link]} location]
            [:li [:a {:href link} name]])]]
    [:h2 "Drops"]
    [:ul (for [{:keys [name link chance]} drops]
           [:li [:a {:href link} (str name " %" chance)]])]]))

(defn location-page
  "Location Page"
  [{:keys [title description enemies]}]
  (html5 [:div
          [:h1 title]
          [:hr]
          [:p description]]))

(defn weapon-page
  "Weapon Page"
  [{:keys [title description]}]
  (html5 [:div
          [:h1 title]
          [:hr]
          [:p description]]))

(defn about-page
  "About page"
  [page]
  [:div
   [:h1 "How To Play Wiki"]
   [:p "This site is an experiment to create a fast, simple, and accuate gaming wiki for Elden Ring utilizing static site generation, clojure, and crowd-sourced contributions."]
   [:p "How To Play Wiki will never be host to ads, needless JavaScript, tracking, or anything that doesn't have to do directly with the game in question."]
   [:h1 "I'd like to help!"]
   [:p "Currently this wiki is being worked on, but once we're at MVP you can contribute "
    [:a {:href "https://github.com/SoulSloth/how-to-play-wiki-content"} "to the content wiki."]]
   [:h1 "Who are you?"]
   [:p "I'm Soul. You can catch my other stuff at my site: "
    [:a {:href "https://soulreviews.net/"} "soulreviews.net"]]])
