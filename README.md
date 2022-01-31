# media-server

This project is a simple media server implementation made in Kotlin and HTML, it is nowhere near complete and is not intended to be used in any production or for any public application, this project was made to learn and share.

## Features:
- Independant (no need for nginx etc...)
- Out of the box functionallity.
- Authentication for uploading
- Username and password hashing (extra security)
- File persistance on disk

## How to run:
1. Change variables in the code to your specifications, (username, password, hash method, etc...)
2. Build jar.
3. Upload jar to server or intended destination.
4. Run jar.

## Usage:
- /upload - Main page where you can upload files. (Requires login)
- /uploads/{id} - Pages where uploaded media can be accessed.
- /list - Page where all uploads can be listed. (Requires login)

If you want to use this project , please link original and credit this page.
