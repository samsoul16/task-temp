(ns abcd.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-greeting
 (fn [db _]
   (:greeting db)))

(reg-sub
 :get-tasks
 (fn [db _]
   (:org-tasks db)))
