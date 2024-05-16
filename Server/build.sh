gcc main.c Socket.c Database.c log.c dictionary.c -o main -I/usr/include/postgresql -lpq -DLOG_USE_COLOR 
#solo per alex