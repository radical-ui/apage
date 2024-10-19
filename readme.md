# apage

A page-based [Objection](https://github.com/radical-ui/objection) frontend for Android.

## Technical Debt

- Build scripts do not read from the objection config
- No preview script
- Form inputs do not display loaders
- Pages are not watched ahead of time the are to be displayed, and are unwatched immediately
- Loaders (skeleton, ideally) are not shown during the time a page is loading 
- "Loading..." and "Error" views are terrible
- No offline support
- Connection retrying does not take into account unacknowledged messages
