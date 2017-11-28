(ns abcd.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::greeting string?)
(s/def ::app-db
  (s/keys :req-un [::greeting]))

;; initial state of app-db
(def app-db {:greeting "Hello Clojurescript in Expo!"
             :org-tasks [{:id 1 :parent 0 :task "Task 1"}   {:id 2 :parent 0 :task "Task 2"}
                         {:id 3 :parent 1 :task "Task 3"}   {:id 4 :parent 1 :task "Task 4"}   {:id 5 :parent 1 :task "Task 5"}
                         {:id 6 :parent 2 :task "Task 6"}   {:id 7 :parent 2 :task "Task 7"}   {:id 8 :parent 2 :task "Task 8"}
                         {:id 9 :parent 3 :task "Task 9"}   {:id 10 :parent 3 :task "Task 10"}
                         {:id 11 :parent 4 :task "Task 11"} {:id 12 :parent 4 :task "Task 12"}
                         {:id 13 :parent 5 :task "Task 13"}
                         {:id 14 :parent 6 :task "Task 14"} {:id 15 :parent 6 :task "Task 15"}
                         {:id 16 :parent 7 :task "Task 16"}
                         {:id 17 :parent 8 :task "Task 17"}]})
