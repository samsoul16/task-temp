(ns abcd.core
    (:require [reagent.core :as r :refer [atom]]
              [re-frame.core :refer [subscribe dispatch dispatch-sync]]
              [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
              [abcd.handlers]
              [abcd.subs]))

(def env (.-env js/process))

(when (= (.-NODE_ENV env) "development")
  (set! (.-REACT_NAV_LOGGING (.-env js/process)) true))

(def ReactNative (js/require "react-native"))
(def ReactNavigation (js/require "react-navigation"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def Alert (.-Alert ReactNative))
(defn alert [title]
  (.alert Alert title))

(def add-icon (js/require "./assets/images/add.png"))
(defonce Expo (js/require "expo"))
(defonce secure-store (.-SecureStore Expo))
(defn ex-comp [name]
  (-> Expo
      (aget name)
      r/adapt-react-class))

(def RNSD (js/require "react-native-simple-dialogs"))
(def confirm-dialog (r/adapt-react-class (aget RNSD "ConfirmDialog")))

(defn json-generate
  "Returns a newline-terminate JSON string from the given ClojureScript data."
  [data]
  (str (.stringify js/JSON (clj->js data)) "\n"))

(defn json-parse
  "Returns ClojureScript data for the given JSON string."
  [line]
  (js->clj (.parse js/JSON line) :keywordize-keys true))

(defn save-to-store [token-key token-value]
  (.catch (.then (.setItemAsync secure-store token-key token-value (clj->js {}))
                 #(println "Saved " token-key))
          #(println "Couldnot save, error >>" %)))

(defn get-from-store [token-key]
  (let [token-value (r/atom nil)]
    (.catch (.then (.getItemAsync secure-store token-key (clj->js {}))
                   (fn [val] (if (nil? val)
                               (println "NOT GOT")
                               (do (println "GOT" (json-parse val))
                                   (reset! token-value (json-parse val))
                                   (println (:id (first @token-value)))))))
            (fn [e] (println "Error in Retrieving from store" e)))))

(defn expo-store [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [navigate goBack]} navigation]
      [view {:style {:flex 1 :flex-direction "column" :margin 10 :justify-content "center" :align-items "center"}}
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(println data)}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Print Cljs Map Data"]]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(println (json-generate data))}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Convert Clj -> Json String"]]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(save-to-store "my-data" (json-generate data))}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Store Json String in Expo"]]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(println (get-from-store "my-data"))}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Get Json String from Expo"]]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(println (json-parse (get-from-store "my-data")))}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Json -> Clj Data from Expo"]]])))

(defn default-app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [image {:source (js/require "./assets/images/cljs.png")
               :style {:width 200
                       :height 200}}]
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]])))


(defn Main [props]
  (fn [{:keys [screenProps navigation] :as props}]
    (let [{:keys [state navigate goBack]} navigation
          idx (:id (:params state))
          tasks (subscribe [:get-tasks])
          filtered (filter #(= (:parent %) idx) @tasks)]
      [view {:style {:margin 10 :flex 1 :justify-content "center" :align-items "center"}}
       (println @tasks)
       (map-indexed
        (fn [idx task]
          ^{:key idx}
          [touchable-highlight {:style {:background-color "#ffc" :padding 10 :border-radius 5 :width "100%"}
                                :on-press #(navigate "Main" {:id (:id task)})}
           [text {:style {:text-align "center" :font-weight "bold"}} (:task task)]])
        filtered)
       [touchable-highlight {:style {:position "absolute" :bottom 0
                                     :background-color "#f00" :padding 10 :border-radius 5 :width "100%"}
                             :on-press #(alert "ADD A NEW TASK")}
        [text {:style {:text-align "center" :font-weight "bold"}} "ADD NEW TASK"]]])))

(def MainStack (stack-navigator {:Main {:screen (stack-screen Main {:title "Main"})}}
                                {:intialRouteName "Main"
                                 :initialRouteParams {:id 0}}))

(defn app-root []
  (fn []
    [:> MainStack {}]))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "main" #(r/reactify-component app-root)))
