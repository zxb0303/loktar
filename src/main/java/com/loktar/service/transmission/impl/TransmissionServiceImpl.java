package com.loktar.service.transmission.impl;


import com.loktar.domain.transmission.TrTorrent;
import com.loktar.domain.transmission.TrTorrentTracker;
import com.loktar.dto.transmission.TrResponseDTO;
import com.loktar.dto.transmission.TrResponseTorrentDTO;
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

    public TransmissionServiceImpl(TrTorrentMapper trTorrentMapper, TrTorrentTrackerMapper trTorrentTrackerMapper) {
        this.trTorrentMapper = trTorrentMapper;
        this.trTorrentTrackerMapper = trTorrentTrackerMapper;
    }


    @Override
    public TrResponseDTO getFreeSpaceByDownloadDir(String downloadDir) {
        return TransmissionUtil.getFreeSpaceByPath(downloadDir);
    }

    @Override
    public TrResponseDTO refreshAllTorrents() {
        TrResponseDTO trResponseDTO = TransmissionUtil.getAllTorrents();
        if (ObjectUtils.isEmpty(trResponseDTO)) {
            return null;
        }
        List<TrResponseTorrentDTO> trResponseTorrentDTOs = trResponseDTO.getArguments().getTorrents();
        for (TrResponseTorrentDTO trResponseTorrentDTO : trResponseTorrentDTOs) {
            TrTorrent trTorrent = changeTrTorrentDomain(trResponseTorrentDTO);
            trTorrentMapper.insertOrUpdate(trTorrent);
            for (TrTorrentTracker trTorrentTracker : trResponseTorrentDTO.getTrackerStats()) {
                trTorrentTracker.setTorrentId(trTorrent.getId());
                trTorrentTracker.setId(Integer.valueOf(String.valueOf(trTorrent.getId()) + String.valueOf(trTorrentTracker.getId())));
                trTorrentTracker.setHost(trTorrentTracker.getHost().replace("https://", "").replace("http://", "").replace(":443", "").replace(":80", ""));
                trTorrentTrackerMapper.insertOrUpdate(trTorrentTracker);
            }
        }
        return trResponseDTO;
    }

    @Override
    public void autoRemove(Long minSizeGB, int days, String downloadDir) {
        TrResponseDTO trResponseDTO = TransmissionUtil.getFreeSpaceByPath(downloadDir);
        if (ObjectUtils.isEmpty(trResponseDTO)) {
            return;
        }
        long leftSize = trResponseDTO.getArguments().getSizeBytes();
        long minSize = minSizeGB * 1024 * 1024 * 1024;

        if (leftSize <= minSize) {
            //空间不足，删除一下老的种子
            autoRemoveSize(leftSize, minSize, minSizeGB, days, downloadDir);
        }
    }
    @Override
    public void autoRemoveError() {
        List<String> errorNames = trTorrentMapper.getErrorName();
        if (errorNames.size() > 0) {
            System.out.println("自动删除下列错误种子：");
        }
        for (String name : errorNames) {
            System.out.println(name);
            List<TrTorrent> trTorrents = trTorrentMapper.getTorrentsByName(name);
            List<Integer> removeIds = new ArrayList<Integer>();
            if (trTorrents.size() == 1) {
                //错误的只有1个时，删种删文件
                TrTorrent errorTrTorrent = trTorrents.get(0);
                removeIds.add(errorTrTorrent.getId());
                trTorrentMapper.deleteByPrimaryKey(errorTrTorrent.getId());
                trTorrentTrackerMapper.deleteByTorrentId(errorTrTorrent.getId());
                TransmissionUtil.removeTorrents(removeIds.toArray(new Integer[removeIds.size()]), true);
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
                TransmissionUtil.removeTorrents(removeIds.toArray(new Integer[removeIds.size()]), false);
            }
        }
    }

    private void autoRemoveSize(long leftSize, long minSize, Long minSizeGB, int days, String downloadDir) {
        List<Integer> tempIds = new ArrayList<Integer>();
        List<String> tempNames = new ArrayList<>();
        //TODO 打印
        System.out.println("当前空间：" + Math.floor(leftSize / 1024 / 1024 / 1024) + ";不足" + minSizeGB + "GB");
        System.out.println("自动删除下列种子：");
        while (leftSize <= minSize) {
            TrTorrent worstTorrent = trTorrentMapper.getworstTorrent(days, downloadDir);
            if (ObjectUtils.isEmpty(worstTorrent)) {
                return;
            }
            //TODO 打印
            System.out.println(Math.floor(worstTorrent.getTotalSize() / 1024 / 1024 / 1024) + ";" + worstTorrent.getName() + ";" + worstTorrent.getTotalSize().toString());
            tempNames.add(worstTorrent.getName());
            List<TrTorrent> needRemoveTrTorrents = trTorrentMapper.getTorrentsByNameAndSize(worstTorrent.getName(), worstTorrent.getTotalSize());
            for (TrTorrent needRemoveTrTorrent : needRemoveTrTorrents) {
                trTorrentMapper.deleteByPrimaryKey(needRemoveTrTorrent.getId());
                trTorrentTrackerMapper.deleteByTorrentId(needRemoveTrTorrent.getId());
                tempIds.add(needRemoveTrTorrent.getId());
            }
            leftSize = leftSize + worstTorrent.getTotalSize();
        }
        TrResponseDTO needRemoveTrResponseDTO = TransmissionUtil.getTorrents(tempIds.toArray(new Integer[tempIds.size()]));
        List<TrResponseTorrentDTO> needRemoveTorrents = needRemoveTrResponseDTO.getArguments().getTorrents();
        List<Integer> trueRemoveIds = new ArrayList<Integer>();
        for (TrResponseTorrentDTO needRemoveTorrent : needRemoveTorrents) {
            if (needRemoveTorrent.getDownloadDir().equals(TransmissionUtil.TEMP_DOWNLOAD_DIR) && tempNames.contains(needRemoveTorrent.getName())) {
                trueRemoveIds.add(Integer.valueOf(String.valueOf(needRemoveTorrent.getId())));
            }
        }
        TransmissionUtil.removeTorrents(trueRemoveIds.toArray(new Integer[trueRemoveIds.size()]), true);
    }


    @Override
    public void autoStart() {
        List<TrTorrent> needStartTrTorrents = trTorrentMapper.getNeedStartTrTorrents();
        if (needStartTrTorrents.size() == 0) {
            return;
        }
        List<Integer> ids = new ArrayList<Integer>();
        for (TrTorrent needStartTrTorrent : needStartTrTorrents) {
            ids.add(needStartTrTorrent.getId());
            needStartTrTorrent.setStatus(6);
            trTorrentMapper.updateByPrimaryKey(needStartTrTorrent);
        }
        TransmissionUtil.startTorrents(ids.toArray(new Integer[needStartTrTorrents.size()]));

    }

    //TODO 待修改
    private TrTorrent changeTrTorrentDomain(TrResponseTorrentDTO trResponseTorrentDTO) {



//        String str = JSONObject.toJSONString(trResponseTorrentDTO);
//        TrTorrent trTorrent = JSONObject.parseObject(str, TrTorrent.class);
//        return trTorrent;
        return null;
    }

}
