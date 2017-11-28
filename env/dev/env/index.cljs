(ns env.index
  (:require [env.dev :as dev]))

;; undo main.js goog preamble hack
(set! js/window.goog js/undefined)

(-> (js/require "figwheel-bridge")
    (.withModules #js {"./assets/icons/loading.png" (js/require "../../../assets/icons/loading.png"), "expo" (js/require "expo"), "./assets/images/cljs.png" (js/require "../../../assets/images/cljs.png"), "./assets/icons/app.png" (js/require "../../../assets/icons/app.png"), "react-native-simple-dialogs" (js/require "react-native-simple-dialogs"), "react-native" (js/require "react-native"), "react-navigation" (js/require "react-navigation"), "./assets/images/add.png" (js/require "../../../assets/images/add.png"), "react" (js/require "react"), "create-react-class" (js/require "create-react-class")}
)
    (.start "main" "expo" "192.168.0.127"))
