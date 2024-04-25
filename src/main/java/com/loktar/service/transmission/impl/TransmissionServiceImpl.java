package com.loktar.service.transmission.impl;


import com.loktar.conf.LokTarConfig;
import com.loktar.domain.transmission.TrTorrent;
import com.loktar.domain.transmission.TrTorrentTracker;
import com.loktar.dto.transmission.TrResponse;
import com.loktar.dto.transmission.TrResponseTorrent;
import com.loktar.mapper.transmission.TrTorrentMapper;
import com.loktar.mapper.transmission.TrTorrentTrackerMapper;
import com.loktar.service.transmission.TransmissionService;
import com.loktar.util.TransmissionUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransmissionServiceImpl implements TransmissionService {

    private final TrTorrentMapper trTorrentMapper;

    private final TrTorrentTrackerMapper trTorrentTrackerMapper;

    private final TransmissionUtil transmissionUtil;

    private final LokTarConfig lokTarConfig;

    public TransmissionServiceImpl(TrTorrentMapper trTorrentMapper, TrTorrentTrackerMapper trTorrentTrackerMapper, TransmissionUtil transmissionUtil, LokTarConfig lokTarConfig) {
        this.trTorrentMapper = trTorrentMapper;
        this.trTorrentTrackerMapper = trTorrentTrackerMapper;
        this.transmissionUtil = transmissionUtil;
        this.lokTarConfig = lokTarConfig;
    }


    @Override
    public TrResponse getFreeSpaceByDownloadDir(String downloadDir) {
        return transmissionUtil.getFreeSpaceByPath(downloadDir);
    }

    @Override
    public TrResponse refreshAllTorrents() {
        trTorrentMapper.truncate();
        trTorrentTrackerMapper.truncate();
        TrResponse trResponse = transmissionUtil.getAllTorrents();
        if (ObjectUtils.isEmpty(trResponse)) {
            return null;
        }
        List<TrResponseTorrent> trResponseTorrents = trResponse.getArguments().getTorrents();
        trTorrentMapper.insertBatch(trResponseTorrents);

        List<TrTorrentTracker> trTorrentTrackers = new ArrayList<>();
        for (TrResponseTorrent trResponseTorrent : trResponseTorrents) {
            for (TrTorrentTracker trTorrentTracker : trResponseTorrent.getTrackerStats()) {
                trTorrentTracker.setTorrentId(trResponseTorrent.getId());
                trTorrentTracker.setHost(trTorrentTracker.getHost().replace("https://", "").replace("http://", "").replace(":443", "").replace(":80", ""));
                trTorrentTrackers.add(trTorrentTracker);
            }
        }
        trTorrentTrackerMapper.insertBatch(trTorrentTrackers);
        return trResponse;
    }

    @Override
    public void autoRemove(Long minSizeGB, int days, String downloadDir) {
        TrResponse trResponse = transmissionUtil.getFreeSpaceByPath(downloadDir);
        if (ObjectUtils.isEmpty(trResponse)) {
            return;
        }
        long leftSize = trResponse.getArguments().getSizeBytes();
        long minSize = minSizeGB * 1024 * 1024 * 1024;

        if (leftSize <= minSize) {
            //空间不足，删除一下老的种子
            autoRemoveSize(leftSize, minSize, minSizeGB, days, downloadDir);
        }
    }
    @Override
    public void autoRemoveError() {
        List<String> errorNames = trTorrentMapper.getErrorName();
        if (!errorNames.isEmpty()) {
            System.out.println("自动删除下列错误种子：");
        }
        for (String name : errorNames) {
            System.out.println(name);
            List<TrTorrent> trTorrents = trTorrentMapper.getTorrentsByName(name);
            List<Integer> removeIds = new ArrayList<>();
            if (trTorrents.size() == 1) {
                //错误的只有1个时，删种删文件
                TrTorrent errorTrTorrent = trTorrents.getFirst();
                removeIds.add(errorTrTorrent.getId());
                trTorrentMapper.deleteByPrimaryKey(errorTrTorrent.getId());
                trTorrentTrackerMapper.deleteByTorrentId(errorTrTorrent.getId());
                transmissionUtil.removeTorrents(removeIds.toArray(new Integer[0]), true);
            }
            if (trTorrents.size() > 1) {
                //错误的>1个时，只删种不文件
                for (TrTorrent t : trTorrents) {
                    if (t.getError() != 0) {
                        removeIds.add(t.getId());
                        trTorrentMapper.deleteByPrimaryKey(t.getId());
                        trTorrentTrackerMapper.deleteByTorrentId(t.getId());
                    }
                }
                transmissionUtil.removeTorrents(removeIds.toArray(new Integer[0]), false);
            }
        }
    }

    private void autoRemoveSize(long leftSize, long minSize, Long minSizeGB, int days, String downloadDir) {
        List<Integer> tempIds = new ArrayList<>();
        List<String> tempNames = new ArrayList<>();
        //TODO 打印
        System.out.println("当前空间：" + Math.floor((double) leftSize / 1024 / 1024 / 1024) + ";不足" + minSizeGB + "GB");
        System.out.println("自动删除下列种子：");
        while (leftSize <= minSize) {
            TrTorrent worstTorrent = trTorrentMapper.getworstTorrent(days, downloadDir);
            if (ObjectUtils.isEmpty(worstTorrent)) {
                return;
            }
            //TODO 打印
            System.out.println(Math.floor((double) worstTorrent.getTotalSize() / 1024 / 1024 / 1024) + ";" + worstTorrent.getName() + ";" + worstTorrent.getTotalSize().toString());
            tempNames.add(worstTorrent.getName());
            List<TrTorrent> needRemoveTrTorrents = trTorrentMapper.getTorrentsByNameAndSize(worstTorrent.getName(), worstTorrent.getTotalSize());
            for (TrTorrent needRemoveTrTorrent : needRemoveTrTorrents) {
                trTorrentMapper.deleteByPrimaryKey(needRemoveTrTorrent.getId());
                trTorrentTrackerMapper.deleteByTorrentId(needRemoveTrTorrent.getId());
                tempIds.add(needRemoveTrTorrent.getId());
            }
            leftSize = leftSize + worstTorrent.getTotalSize();
        }
        TrResponse needRemoveTrResponse = transmissionUtil.getTorrents(tempIds.toArray(new Integer[0]));
        List<TrResponseTorrent> needRemoveTorrents = needRemoveTrResponse.getArguments().getTorrents();
        List<Integer> trueRemoveIds = new ArrayList<>();
        for (TrResponseTorrent needRemoveTorrent : needRemoveTorrents) {
            if (needRemoveTorrent.getDownloadDir().equals(lokTarConfig.getTransmission().getTempDownloadDir()) && tempNames.contains(needRemoveTorrent.getName())) {
                trueRemoveIds.add(Integer.valueOf(String.valueOf(needRemoveTorrent.getId())));
            }
        }
        transmissionUtil.removeTorrents(trueRemoveIds.toArray(new Integer[0]), true);
    }


    @Override
    public void autoStart() {
        List<TrTorrent> needStartTrTorrents = trTorrentMapper.getNeedStartTrTorrents();
        if (needStartTrTorrents.isEmpty()) {
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (TrTorrent needStartTrTorrent : needStartTrTorrents) {
            ids.add(needStartTrTorrent.getId());
            needStartTrTorrent.setStatus(6);
            trTorrentMapper.updateByPrimaryKey(needStartTrTorrent);
        }
        transmissionUtil.startTorrents(ids.toArray(new Integer[needStartTrTorrents.size()]));
    }
}
