(ns how-to-play-wiki.pages
  (:require [stasis.core :as stasis]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]
            [optimus.link :as link]
            [markdown.core :as md]
            [clojure.string :as str]))

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

(def stat-order
  ["level" "vitality" "attunement" "endurance" "strength" "dexterity" "resistance" "intelligence" "faith" "humanity"])

(defn character-stat-table
  "Create a table to display character stats"
  [page request]
  [:table.crunch
   [:tr
    (for [name stat-order]
      [:th
       [:img {:src (link/file-path request (str "/assets/stats/" name ".webp")) :alt name :title name}]])]
   [:tr
    (for [stat stat-order]
      [:td (get page (keyword stat))])]])

(defn class-page
  "display information about a class"
  [{:keys [title stats image]} request]
  (html5
   [:div
    [:h2 title]
    [:img {:src (link/file-path request (str "/assets/classes/" title ".webp") )}]
    (character-stat-table stats request)]))

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
