# media-server

This project is a simple media server implementation that allows you to upload and share files. This project was made in Kotlin and HTML, it is nowhere near complete and is not intended to be used in any production or for any public application, this project was made to learn and share.

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

<img width="525" alt="Upload Screen Shot" src="https://user-images.githubusercontent.com/39539212/151848034-1be95232-f39a-4661-8c68-92bc76777bc3.png">


<img width="921" alt="View Screen Shot" src="https://user-images.githubusercontent.com/39539212/151848144-e7c7b4b2-cfe2-48f6-9b6e-ca40ab1c1cae.png">

This project is licensed under the MIT License.
