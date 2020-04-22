package com.songfeelsfinal.songfeels.models;

public class YouTubeAPIRetrofit {
    private Id id;
    private Snippet snippet;


    public Id getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public class Id {

        private String videoId;

        public String getVideoId() {
            return videoId;
        }
    }

    public class Snippet {
        private String publishedAt;
        private String channelId;
        private String title;
        private String description;
        private Thumbnails thumbnails;



        public String getPublishedAt() {
            return publishedAt;
        }

        public String getChannelId() {
            return channelId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Thumbnails getThumbnails() {
            return thumbnails;
        }

        public class Thumbnails {
            private High high;

            public High getHigh() {
                return high;
            }

            public class High {
                private String url;

                public String getUrl() {
                    return url;
                }
            }
        }
    }

}
