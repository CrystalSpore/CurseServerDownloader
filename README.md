# Curse Server Downloader

As the name implies, this is a tool to assist with downloading CurseForge Server Zips.

## Project Purpose

The CurseForge site enabled CloudFlare DDOS protection a while back, preventing server owners (such as myself) from using wget to download server zips.
With this tool, the goal is to once again make it easy for server admins to install their servers WITHOUT needing to scp zip files to the server machine.

## Usage

Not fully implemented yet. However, the goal is to have the run be `CurseServerDownloader [link]` and get the latest zip downloaded into the current directory.