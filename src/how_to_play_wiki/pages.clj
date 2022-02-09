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
    [:h1 "Elden Ring Wiki"]
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
          (filter #(re-find #":category :enemies" (second %)) pages)]
      (let [{title :title category :category} (read-string (second page))]
        [:li [:a {:href (str/replace (first page) #"\.edn$" "/") } title]]))]])

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
