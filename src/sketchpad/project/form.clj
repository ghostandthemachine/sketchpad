(ns sketchpad.project.form
	(:use [seesaw.core]
				[sketchpad.auto-complete.auto-complete])
	(:require [seesaw.forms :as forms]
						[sketchpad.rsyntax :as rsyntax]))

(defn project-creation-form
  []
  (let [repo-text-area (rsyntax/text-area :columns 200)
				form	(forms/forms-panel
								  "pref,4dlu,80dlu,8dlu,pref,4dlu,80dlu"
								  :column-groups [[1 5]]
								  :items [(forms/separator "General")
								          "Company" (forms/span (text) 5)
								          "Contact" (forms/span (text) 5)
								          (forms/separator "Propeller")
								          "PTI/kW"  repo-text-area "Power/kW" (text :columns 10)]
								  :default-dialog-border? true)]
		(install-clojars-auto-completions repo-text-area)
		form))

(defn form-dialog []
	(dialog :content (project-creation-form)))