Optional next improvement (recommend)
Right now processNotificationsSending() marks notifications as dispatched even if the user isn’t connected. If you want to guarantee delivery, I can change it to:

Check userRegistry.getUser(username)
Only set dispatched = true when connected
Otherwise leave it queued for next connect/subscription