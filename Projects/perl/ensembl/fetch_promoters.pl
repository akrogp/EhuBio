#!/usr/bin/perl

use Bio::EnsEMBL::Registry;

Bio::EnsEMBL::Registry->load_registry_from_db(
	-host => 'ensembldb.ensembl.org',
	-user => 'anonymous'
);

my $regulatory_feature_adaptor = Bio::EnsEMBL::Registry->get_adaptor('Human', 'Funcgen', 'RegulatoryFeature');

my $regulatory_features = $regulatory_feature_adaptor->fetch_all();

for my $rf (@{$regulatory_features}) {
	if ($rf->feature_type->name eq 'Promoter'){
		printf( ">%s %s=%s region=%d-%d strand=%+d genes=",
				$rf->display_id(),
				$rf->coord_system_name(), $rf->seq_region_name(),
				$rf->seq_region_start(), $rf->seq_region_end(), $rf->seq_region_strand()
			);
		my $genes = $rf->feature_Slice->get_all_Genes();
		for my $gene (@{$genes}) {
			print $gene->stable_id(), ","
		}
		print "\n", $rf->feature_Slice->seq, "\n";
	}
}
