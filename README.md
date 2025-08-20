# Built by Pixel NewsUK Android Task Submission

Thanks for taking the time to review my submission for the Built by Pixel Android task.

This was a fun project that expanded upon my experience and I had a lot of fun making it.

I attempted to write clean, SOLID code, demonstrating separation of concerns, clean architecture and sound MVVM design decisions.

The project is scalable, I included a navigation component in case more screens need to be added.

Different UI states were considered by my use of `rememberSaveable`, using just `remember` would have lost a coin selection on orientation change

I attempted to animate the list by using a scroll to top button, which smoothly brings the user to the top of the list in order to refresh

---

### Improvements:
- More tests would be advantageous
- I would also like to make it adapt to light and dark theme, rather than not using the proper Android theming at all in the interest of time
- I think perhaps the BottomSheet component would be more testable if it took more parameters to hoist state better (like passing the list of coins to the composable)
- I am new to creating hilt annotations for passing a dispatcher to a viewmodel, so I'm unsure if there's a better way to make the VM testable (I struggled getting the VM tests to all pass without doing this Dependency Injection)
- So that it works on a screen of any size, I would avoid using hardcoded dimensions in a production app, but in the interests of time I used some dp values

---

## Thanks again!
