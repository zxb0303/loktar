package com.loktar.dto.transmission;

import com.loktar.domain.transmission.TrTorrent;
import com.loktar.domain.transmission.TrTorrentTracker;
import lombok.Data;

import java.util.List;

@Data
public class TrResponseTorrent extends TrTorrent {
    private List<TrTorrentTracker> trackerStats;
}
