package it.polimi.dima.track.data

import it.polimi.dima.track.model.User

val anonymousUser = User(
  id = "anonymousUserId",
  isAnonymous = true,
  name = "Anonymous John Doe"
)

val registeredUser = User(
  id = "registeredUserId",
  isAnonymous = false,
  name = "Registered John Doe",
)
