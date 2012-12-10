//My Cloud Portal - Self Service Portal for the cloud.
//This file is part of My Cloud Portal.
//
//My Cloud Portal is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, version 3 of the License.
//
//My Cloud Portal is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with My Cloud Portal.  If not, see <http://www.gnu.org/licenses/>.

package in.mycp.remote;

import in.mycp.domain.AddressInfoP;
import in.mycp.domain.Asset;
import in.mycp.domain.AssetType;
import in.mycp.domain.Company;
import in.mycp.domain.Department;
import in.mycp.domain.GroupDescriptionP;
import in.mycp.domain.InstanceP;
import in.mycp.domain.KeyPairInfoP;
import in.mycp.domain.Project;
import in.mycp.domain.SnapshotInfoP;
import in.mycp.domain.User;
import in.mycp.domain.VolumeInfoP;
import in.mycp.utils.Commons;
import in.mycp.web.DashboardDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

/**
 * 
 * Assets - Asset details, AssetType, AssetUser, Start , end , rate , Cost,-
 * order by User
 * 
 * Company - Asset details, AssetType, AssetUser, Start , end , rate , Cost,
 * 
 * Department - Asset details, AssetType, AssetUser, Start , end , rate ,Cost,
 * 
 * Project - Asset details, AssetType, AssetUser, Start , end , rate , Cost,
 * 
 * User - Asset details, AssetType, Start , end , rate , Cost,
 * 
 * 
 * 
 * 
 * @author Charudath Doddanakatte
 * @author cgowdas@gmail.com
 *
 */
 
@RemoteProxy(name = "ReportService")
public class ReportService {

	private static final Logger log = Logger.getLogger(ReportService.class.getName());

	public List<Asset> getAssets(String currentRole, User currentUser, AssetType assetType, boolean billable, boolean active) {
		// if superadmin , get all asset report
		if (currentRole.equals(Commons.ROLE.ROLE_SUPERADMIN + "")) {
			return Asset.findAssets4Report4User(0, assetType, billable, active).getResultList();
			// if not , get owner asset report only
		} else if (currentRole.equals(Commons.ROLE.ROLE_MANAGER + "")) {
			return Asset.findAssets4Report4Company(Commons.getCurrentSession().getCompanyId(), assetType, billable, active).getResultList();
		} else {
			return Asset.findAssets4Report4User(currentUser.getId(), assetType, billable, active).getResultList();
		}
	}

	public long getAssetCost(Asset asset) {
		long duration =0;
		if (asset.getEndTime() == null) {
			duration = new Date().getTime() - asset.getStartTime().getTime();
		}else{
			duration = asset.getEndTime().getTime() - asset.getStartTime().getTime();	
		}
		
		duration = (duration / (1000 * 60 * 60));
		return (asset.getProductCatalog().getPrice() * duration);

	}

	public List<Asset> fillCommon(Asset asset, List<Asset> assets2return) {
		long duration =0;
		if (asset.getEndTime() == null) {
			duration = new Date().getTime() - asset.getStartTime().getTime();
		}else{
			duration = asset.getEndTime().getTime() - asset.getStartTime().getTime();	
		}
		
		asset.setDuration((duration / (1000 * 60 * 60)));

		if (asset.getProductCatalog() != null) {
			asset.setAssetTypeName(asset.getProductCatalog().getName());
			asset.setCost(asset.getDuration() * asset.getProductCatalog().getPrice());
			asset.setStartRate(asset.getProductCatalog().getPrice());
			asset.setCurrency(asset.getProductCatalog().getCurrency());
		}

		assets2return.add(asset);
		return assets2return;
	}

	// User wise Assets
	// Project wise Assets
	// Company wise assets
	// Account - department- project - user

	@RemoteMethod
	public List<Asset> findAllShort() {
		try {

			List<Asset> assets2return = new ArrayList<Asset>();

			User currentUser = Commons.getCurrentUser();
			String currentRole = currentUser.getRole().getName();
			// currentUser = User.findUser(currentUser.getId());

			boolean billable = true;
			boolean active = true;
			List<AssetType> allAssetTypes = AssetType.findAllAssetTypes();
			for (Iterator iterator = allAssetTypes.iterator(); iterator.hasNext();) {
				try {
					List<Asset> assets = null;
					AssetType assetType = (AssetType) iterator.next();
					if (!assetType.getBillable()) {
						continue;
					}
					if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeImage)) {
						// do nothing since we are not able to create imgaes on
						// the fly now in euca
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeInstance)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							assets2return = fillCommonComputeInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpAddress)
							|| assetType.getName().equals("" + Commons.ASSET_TYPE.addressInfo)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							assets2return = fillCommonAddressInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpPermission)) {
						// Do you really want to charge for opening holes in
						// customers firewall?
						/*
						 * if(asset.getAssetType().getBillable()){
						 * 
						 * }else{ continue; }
						 */
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.KeyPair)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							assets2return = fillCommonKeypairInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.SecurityGroup)) {

						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							assets2return = fillCommonSecurityGroupInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.Volume)) {

						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							assets2return = fillCommonVolumeInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.VolumeSnapshot)) {

						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							assets2return = fillCommonSnapshotInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else {
						log.error("Which asset does this workflow belong?");
					}

				} catch (Exception e) {
					log.error(e.getMessage());// e.printStackTrace();
				}
			}

			return assets2return;

		} catch (Exception e) {

			log.error(e.getMessage());// e.printStackTrace();
		}

		return null;

	}// end of method findAll

	@RemoteMethod
	public DashboardDTO getAllAssetCosts() {
		Date start = new Date();
		try {

			List<Asset> assets2return = new ArrayList<Asset>();

			User currentUser = Commons.getCurrentUser();
			String currentRole = currentUser.getRole().getName();
			// currentUser = User.findUser(currentUser.getId());
			DashboardDTO dto = new DashboardDTO();
			try {
				dto.setCurrency(Company.findCompany(Commons.getCurrentSession().getCompanyId()).getCurrency());
			} catch (Exception e) {
				// e.printStackTrace();
			}

			boolean billable = true;
			boolean active = true;
			List<AssetType> allAssetTypes = AssetType.findAllAssetTypes();
			for (Iterator iterator = allAssetTypes.iterator(); iterator.hasNext();) {
				try {
					List<Asset> assets = null;
					AssetType assetType = (AssetType) iterator.next();
					if (!assetType.getBillable()) {
						continue;
					}
					if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeImage)) {
						// do nothing since we are not able to create imgaes on
						// the fly now in euca
						dto.setImageCost(0);
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeInstance)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);

							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setComputeCost(dto.getComputeCost() + getAssetCost(asset));

								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpAddress)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setIpaddressCost(dto.getIpaddressCost() + getAssetCost(asset));

								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpPermission)) {

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.KeyPair)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setKeyCost(dto.getKeyCost() + getAssetCost(asset));

								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.SecurityGroup)) {

						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setSecgroupCost(dto.getSecgroupCost() + getAssetCost(asset));

								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.Volume)) {

						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setVolumeCost(dto.getVolumeCost() + getAssetCost(asset));

								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.VolumeSnapshot)) {
						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setSnapshotCost(dto.getSnapshotCost() + getAssetCost(asset));

								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.addressInfo)) {

						try {
							assets = getAssets(currentRole, currentUser, assetType, billable, active);
							for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
								try {
									Asset asset = (Asset) iterator2.next();
									dto.setIpaddressCost(dto.getIpaddressCost() + getAssetCost(asset));
								} catch (Exception e) {
									log.error(e.getMessage());// e.printStackTrace();
								}
							}
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else {
						log.error("Which asset does this workflow belong?");
						// throw new
						// Exception("Which asset does this workflow belong?");
					}

				} catch (Exception e) {
					log.error(e.getMessage());// e.printStackTrace();
				}
			}

			Date end = new Date();
			long timeTaken = end.getTime() - start.getTime();
			log.debug("timeTaken (s) = " + timeTaken / 1000);

			return dto;

		} catch (Exception e) {

			log.error(e.getMessage());// e.printStackTrace();
		}

		return null;

	}// end of method findAll

	public List<Asset> fillCommonVolumeInfo(List<Asset> assets, List<Asset> assets2return) {
		for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
			try {
				Asset asset = (Asset) iterator2.next();
				VolumeInfoP volume = VolumeInfoP.findVolumeInfoPsByAsset(asset).getSingleResult();
				asset.setAssetDetails(volume.getName() + " " + volume.getVolumeId() + " " + volume.getSize() + "(GB)");
				assets2return = fillCommon(asset, assets2return);
			} catch (Exception e) {
				log.error(e.getMessage());// e.printStackTrace();
			}
		}
		return assets2return;
	}

	public List<Asset> fillCommonComputeInfo(List<Asset> assets, List<Asset> assets2return) {

		for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
			try {
				Asset asset = (Asset) iterator2.next();
				InstanceP instance = InstanceP.findInstancePsByAsset(asset).getSingleResult();
				asset.setAssetDetails(instance.getName() + " " + instance.getDnsName() + " " + instance.getInstanceId() + " "
						+ instance.getInstanceType());
				assets2return = fillCommon(asset, assets2return);
			} catch (Exception e) {
				log.error(e.getMessage());// e.printStackTrace();
			}
		}
		return assets2return;
	}

	public List<Asset> fillCommonSnapshotInfo(List<Asset> assets, List<Asset> assets2return) {
		for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
			try {
				Asset asset = (Asset) iterator2.next();
				SnapshotInfoP snapshot = SnapshotInfoP.findSnapshotInfoPsByAsset(asset).getSingleResult();
				asset.setAssetDetails(snapshot.getSnapshotId() + " " + snapshot.getOwnerId());
				assets2return = fillCommon(asset, assets2return);
			} catch (Exception e) {
				log.error(e.getMessage());// e.printStackTrace();
			}
		}
		return assets2return;
	}

	public List<Asset> fillCommonKeypairInfo(List<Asset> assets, List<Asset> assets2return) {
		for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
			try {
				Asset asset = (Asset) iterator2.next();
				KeyPairInfoP keyPairInfoP = KeyPairInfoP.findKeyPairInfoPsByAsset(asset).getSingleResult();
				asset.setAssetDetails(keyPairInfoP.getKeyName());
				assets2return = fillCommon(asset, assets2return);
			} catch (Exception e) {
				log.error(e.getMessage());// log.error(e.getMessage());//e.printStackTrace();
			}
		}
		return assets2return;
	}

	public List<Asset> fillCommonSecurityGroupInfo(List<Asset> assets, List<Asset> assets2return) {
		for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
			try {
				Asset asset = (Asset) iterator2.next();
				GroupDescriptionP groupDescriptionP = GroupDescriptionP.findGroupDescriptionPsByAsset(asset).getSingleResult();
				asset.setAssetDetails(groupDescriptionP.getName() + " " + groupDescriptionP.getOwner());
				assets2return = fillCommon(asset, assets2return);
			} catch (Exception e) {
				log.error(e.getMessage());// e.printStackTrace();
			}
		}
		return assets2return;
	}

	public List<Asset> fillCommonAddressInfo(List<Asset> assets, List<Asset> assets2return) {
		for (Iterator iterator2 = assets.iterator(); iterator2.hasNext();) {
			try {
				Asset asset = (Asset) iterator2.next();
				AddressInfoP addressInfoP = AddressInfoP.findAddressInfoPsByAsset(asset).getSingleResult();
				asset.setAssetDetails(addressInfoP.getName() + " " + addressInfoP.getPublicIp());
				assets2return = fillCommon(asset, assets2return);
			} catch (Exception e) {
				log.error(e.getMessage());// e.printStackTrace();
			}
		}
		return assets2return;
	}

	@RemoteMethod
	public List<Asset> findAssets4Company(int companyId) {
		try {
			List<Asset> assets2return = new ArrayList<Asset>();
			boolean billable = true;
			boolean active = true;
			List<AssetType> allAssetTypes = AssetType.findAllAssetTypes();
			for (Iterator iterator = allAssetTypes.iterator(); iterator.hasNext();) {
				try {
					List<Asset> assets = null;
					AssetType assetType = (AssetType) iterator.next();
					if (!assetType.getBillable()) {
						continue;
					}
					if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeImage)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeInstance)) {
						try {
							assets = Asset.findAssets4Report4Company(companyId, assetType, billable, active).getResultList();
							assets2return = fillCommonComputeInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpAddress)
							|| assetType.getName().equals("" + Commons.ASSET_TYPE.addressInfo)) {
						try {
							assets = Asset.findAssets4Report4Company(companyId, assetType, billable, active).getResultList();
							assets2return = fillCommonAddressInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpPermission)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.KeyPair)) {
						try {
							assets = Asset.findAssets4Report4Company(companyId, assetType, billable, active).getResultList();
							assets2return = fillCommonKeypairInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.SecurityGroup)) {

						try {
							assets = Asset.findAssets4Report4Company(companyId, assetType, billable, active).getResultList();

							assets2return = fillCommonSecurityGroupInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.Volume)) {

						try {
							assets = Asset.findAssets4Report4Company(companyId, assetType, billable, active).getResultList();

							assets2return = fillCommonVolumeInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.VolumeSnapshot)) {

						try {
							assets = Asset.findAssets4Report4Company(companyId, assetType, billable, active).getResultList();

							assets2return = fillCommonSnapshotInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else {
						log.error("Which asset does this workflow belong?");
					}

				} catch (Exception e) {
					log.error(e.getMessage());// e.printStackTrace();
				}
			}
			return assets2return;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method find4comapny
	
	@RemoteMethod
	public List<Asset> findAssets4Department(int departmentId) {
		try {
			List<Asset> assets2return = new ArrayList<Asset>();
			boolean billable = true;
			boolean active = true;
			List<AssetType> allAssetTypes = AssetType.findAllAssetTypes();
			for (Iterator iterator = allAssetTypes.iterator(); iterator.hasNext();) {
				try {
					List<Asset> assets = null;
					AssetType assetType = (AssetType) iterator.next();
					if (!assetType.getBillable()) {
						continue;
					}
					if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeImage)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeInstance)) {
						try {
							assets = Asset.findAssets4Report4Department(departmentId, assetType, billable, active).getResultList();
							assets2return = fillCommonComputeInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpAddress)
							|| assetType.getName().equals("" + Commons.ASSET_TYPE.addressInfo)) {
						try {
							assets = Asset.findAssets4Report4Department(departmentId, assetType, billable, active).getResultList();
							assets2return = fillCommonAddressInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpPermission)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.KeyPair)) {
						try {
							assets = Asset.findAssets4Report4Department(departmentId, assetType, billable, active).getResultList();
							assets2return = fillCommonKeypairInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.SecurityGroup)) {

						try {
							assets = Asset.findAssets4Report4Department(departmentId, assetType, billable, active).getResultList();

							assets2return = fillCommonSecurityGroupInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.Volume)) {

						try {
							assets = Asset.findAssets4Report4Department(departmentId, assetType, billable, active).getResultList();

							assets2return = fillCommonVolumeInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.VolumeSnapshot)) {

						try {
							assets = Asset.findAssets4Report4Department(departmentId, assetType, billable, active).getResultList();

							assets2return = fillCommonSnapshotInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else {
						log.error("Which asset does this workflow belong?");
					}

				} catch (Exception e) {
					log.error(e.getMessage());// e.printStackTrace();
				}
			}
			return assets2return;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method find4department
	

	@RemoteMethod
	public List<Asset> findAssets4Project(int projectId) {
		try {
			List<Asset> assets2return = new ArrayList<Asset>();
			boolean billable = true;
			boolean active = true;
			List<AssetType> allAssetTypes = AssetType.findAllAssetTypes();
			for (Iterator iterator = allAssetTypes.iterator(); iterator.hasNext();) {
				try {
					List<Asset> assets = null;
					AssetType assetType = (AssetType) iterator.next();
					if (!assetType.getBillable()) {
						continue;
					}
					if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeImage)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeInstance)) {
						try {
							assets = Asset.findAssets4Report4Project(projectId, assetType, billable, active).getResultList();
							assets2return = fillCommonComputeInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpAddress)
							|| assetType.getName().equals("" + Commons.ASSET_TYPE.addressInfo)) {
						try {
							assets = Asset.findAssets4Report4Project(projectId, assetType, billable, active).getResultList();
							assets2return = fillCommonAddressInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpPermission)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.KeyPair)) {
						try {
							assets = Asset.findAssets4Report4Project(projectId, assetType, billable, active).getResultList();
							assets2return = fillCommonKeypairInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.SecurityGroup)) {

						try {
							assets = Asset.findAssets4Report4Project(projectId, assetType, billable, active).getResultList();

							assets2return = fillCommonSecurityGroupInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.Volume)) {

						try {
							assets = Asset.findAssets4Report4Project(projectId, assetType, billable, active).getResultList();

							assets2return = fillCommonVolumeInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.VolumeSnapshot)) {

						try {
							assets = Asset.findAssets4Report4Project(projectId, assetType, billable, active).getResultList();

							assets2return = fillCommonSnapshotInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else {
						log.error("Which asset does this workflow belong?");
					}

				} catch (Exception e) {
					log.error(e.getMessage());// e.printStackTrace();
				}
			}
			return assets2return;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method find4project

	
	@RemoteMethod
	public List<Asset> findAssets4User(int userId) {
		try {
			List<Asset> assets2return = new ArrayList<Asset>();
			boolean billable = true;
			boolean active = true;
			List<AssetType> allAssetTypes = AssetType.findAllAssetTypes();
			for (Iterator iterator = allAssetTypes.iterator(); iterator.hasNext();) {
				try {
					List<Asset> assets = null;
					AssetType assetType = (AssetType) iterator.next();
					if (!assetType.getBillable()) {
						continue;
					}
					if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeImage)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.ComputeInstance)) {
						try {
							assets = Asset.findAssets4Report4User(userId, assetType, billable, active).getResultList();
							assets2return = fillCommonComputeInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpAddress)
							|| assetType.getName().equals("" + Commons.ASSET_TYPE.addressInfo)) {
						try {
							assets = Asset.findAssets4Report4User(userId, assetType, billable, active).getResultList();
							assets2return = fillCommonAddressInfo(assets, assets2return);
						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.IpPermission)) {
					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.KeyPair)) {
						try {
							assets = Asset.findAssets4Report4User(userId, assetType, billable, active).getResultList();
							assets2return = fillCommonKeypairInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.SecurityGroup)) {

						try {
							assets = Asset.findAssets4Report4User(userId, assetType, billable, active).getResultList();

							assets2return = fillCommonSecurityGroupInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.Volume)) {

						try {
							assets = Asset.findAssets4Report4User(userId, assetType, billable, active).getResultList();

							assets2return = fillCommonVolumeInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else if (assetType.getName().equals("" + Commons.ASSET_TYPE.VolumeSnapshot)) {

						try {
							assets = Asset.findAssets4Report4User(userId, assetType, billable, active).getResultList();

							assets2return = fillCommonSnapshotInfo(assets, assets2return);

						} catch (Exception e) {
							log.error(e.getMessage());// e.printStackTrace();
						}

					} else {
						log.error("Which asset does this workflow belong?");
					}

				} catch (Exception e) {
					log.error(e.getMessage());// e.printStackTrace();
				}
			}
			return assets2return;
		} catch (Exception e) {
			log.error(e.getMessage());// e.printStackTrace();
		}
		return null;
	}// end of method find4User
	
	
	@RemoteMethod
	public List<Asset> findAssets4AllUsers(int projectId) {
		List<Asset> assets = new ArrayList<Asset>();
			try {
				//List<User> users = User.findUsersByProject(Project.findProject(projectId)).getResultList();
				Set<User> users = Project.findProject(projectId).getUsers();
				for (Iterator iterator = users.iterator(); iterator.hasNext();) {
					try {
						User user = (User) iterator.next();
						assets.addAll(findAssets4User(user.getId()));	
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}catch(Exception e){
				log.error(e.getMessage());
			}
		return assets;
		}//end findAssets4AllUsers
	
	@RemoteMethod
	public List<Asset> findAssets4AllProjects(int departmentId) {
		List<Asset> assets = new ArrayList<Asset>();
		try {
			List<User> users = User.findUsersByDepartment(Department.findDepartment(departmentId)).getResultList();
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				try {
					User user = (User) iterator.next();
					assets.addAll(findAssets4User(user.getId()));	
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return assets;
		}//end findAssets4AllProjects
	
	@RemoteMethod
	public List<Asset> findAssets4AllDepartments(int companyId) {
		List<Asset> assets = new ArrayList<Asset>();
		try {
			List<User> users = User.findUsersByCompany(Company.findCompany(companyId)).getResultList();
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				try {
					User user = (User) iterator.next();
					assets.addAll(findAssets4User(user.getId()));	
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return assets;
		}//end findAssets4AllDepartment
	
	@RemoteMethod
	public List<Asset> findAssets4AllAccounts() {
		List<Asset> assets = new ArrayList<Asset>();
		try {
			List<User> users = User.findAllUsers();
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				try {
					User user = (User) iterator.next();
					assets.addAll(findAssets4User(user.getId()));	
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return assets;
		}//end findAssets4AllDepartment
	
}// end of class ReportService

