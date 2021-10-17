# Curse Server Downloader

As the name implies, this is a tool to assist with downloading CurseForge Server Zips.

## Project Purpose

The CurseForge site enabled CloudFlare DDOS protection a while back, preventing server owners (such as myself) from using wget to download server zips.
With this tool, the goal is to once again make it easy for server admins to install their servers WITHOUT needing to scp zip files to the server machine.

## Usage

Current usage allows for a full or partial server download url from CurseForge to be entered in as a commandline argument.

`java -jar CurseServerDownloader <link>`

If a full download url is provided, then the exact file gets downloaded. If a partial url is provided, then the user gets to select which version they wish to download (NOTE: versions may not be in order. Please verify which version you wish to download.)