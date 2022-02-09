(ns how-to-play-wiki.pages
  (:require [stasis.core :as stasis]
            [hiccup.page :refer [html5]]
            [clojure.java.io :as io]
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
          (filter #(re-find (re-pattern (str ":category " item-category )) (second %)) pages)]
      (let [{title :title category :category} (read-string (second page))]
        [:li [:a {:href (str/replace (first page) #"\.edn$" "/")} title]]))]])

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
   [:p "Great! There are two ways to do so:"]
   [:h2 "Contribute Wiki Content"]
   [:p "You can can edit or add new pages to this wiki by creating a pull request on the content repo here: https://github.com/SoulSloth/how-to-play-wiki-content. Pages are written in .edn files(think JSON, but it's clojure), in the future you'll be able to add more feature-rich text with markdown. Please make sure you read the rules and respond to feedback, once your merge request is approved it'll be merged into the project and the site will be rebuilt with your edits."]
   [:h2 "Contribute To The Static Site Generator"]
   [:p "If your familiar with Clojure, you can make contributions to the static site generator located here: https://github.com/SoulSloth/how-to-play-wiki-generator. This is the program that will take the content repo and construct the pages that constitute this site."]
   [:h1 "Who are you?"]
   [:p "I'm Soul. You can catch my other stuff at my site at soulreviews.net"]])
